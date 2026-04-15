package com.routemaster.RouteMaster.service;

import com.routemaster.RouteMaster.dto.RouteSearchResponseDto;
import com.routemaster.RouteMaster.entity.Transportation;
import com.routemaster.RouteMaster.enums.TransportationType;
import com.routemaster.RouteMaster.exception.InvalidRouteException;
import com.routemaster.RouteMaster.mapper.TransportationMapper;
import com.routemaster.RouteMaster.repository.TransportationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteSearchService {

    private final TransportationRepository transportationRepository;
    private final TransportationMapper transportationMapper;

    @Cacheable(value = "routes", key = "#originId + '-' + #destinationId + '-' + #date")
    public List<RouteSearchResponseDto> searchRoutes(Long originId, Long destinationId, LocalDate date) {

        if (originId.equals(destinationId)) {
            throw new InvalidRouteException("Origin and Destination cannot be the same.");
        }

        log.info("Started Route Search: Origin Id: {}, Destination Id: {}, Date: {}", 
                originId, destinationId, date);

        int dayOfWeek = date.getDayOfWeek().getValue();

        List<Transportation> allDailyRoutes = transportationRepository.findByOperationDaysContaining(dayOfWeek);

        Map<Long, List<Transportation>> routeGraph = allDailyRoutes.stream()
                .collect(Collectors.groupingBy(t -> t.getOrigin().getId()));

        List<RouteSearchResponseDto> validRoutes = new ArrayList<>();
        List<Transportation> currentPath = new ArrayList<>();

        findPathsDFS(originId, destinationId, routeGraph, currentPath, validRoutes);

        log.info("Searching Completed: Result of routes is {}", validRoutes.size());

        return validRoutes;

    }

    private void findPathsDFS(Long currentNodeId, Long targetNodeId,
            Map<Long, List<Transportation>> routeGraph,
            List<Transportation> currentPath,
            List<RouteSearchResponseDto> validRoutes) {

        log.debug("Current Route: {}, Path Distance: {}", currentNodeId, currentPath.size());

        if (currentNodeId.equals(targetNodeId)) {
            if (isValidRoute(currentPath)) {
                log.info("Route found: Path Distance: {}", currentPath.size());
                validRoutes.add(convertToDto(currentPath));

            } else {
                log.warn("Route is wrong");
            }
            return;
        }

        if (currentPath.size() >= 3) {
            log.debug("Route distance can be max 3 segments");
            return;
        }

        List<Transportation> nextSteps = routeGraph.getOrDefault(currentNodeId, Collections.emptyList());

        for (Transportation nextStep : nextSteps) {
            if (!canAddToPath(currentPath, nextStep)) {
                log.info("{} -> {} is wrong path.",
                        nextStep.getOrigin().getLocationCode(),
                        nextStep.getDestination().getLocationCode());
                continue;
            }

            currentPath.add(nextStep);

            findPathsDFS(nextStep.getDestination().getId(), targetNodeId, routeGraph, currentPath, validRoutes);

            currentPath.remove(currentPath.size() - 1);
        }
    }

    private boolean canAddToPath(List<Transportation> path, Transportation nextStep) {
        boolean hasFlight = path.stream().anyMatch(t -> t.getTransportationType() == TransportationType.FLIGHT);
        boolean isNextFlight = nextStep.getTransportationType() == TransportationType.FLIGHT;

        // KURAL: Bir rotada en fazla 1 uçuş olabilir.
        if (hasFlight && isNextFlight)
            return false;

        // KURAL: Uçuştan önce en fazla 1 transfer olabilir.
        // Eğer henüz uçuş yoksa ve çantamızda zaten 1 araç varsa (yani bu 2. araç
        // olacaksa)
        // ve bu araç da uçuş değilse; bu kuralı bozar (T -> T -> F yasak).
        if (!hasFlight && !isNextFlight && path.size() == 1)
            return false;

        return true;
    }

    private boolean isValidRoute(List<Transportation> path) {
        if (path.isEmpty() || path.size() > 3)
            return false;

        long flightCount = path.stream()
                .filter(t -> t.getTransportationType() == TransportationType.FLIGHT)
                .count();

        if (flightCount != 1)
            return false;

        if (path.size() == 1) {
            return path.get(0).getTransportationType() == TransportationType.FLIGHT;
        } else if (path.size() == 2) {
            return (path.get(0).getTransportationType() == TransportationType.FLIGHT
                    && path.get(1).getTransportationType() != TransportationType.FLIGHT) ||
                    (path.get(0).getTransportationType() != TransportationType.FLIGHT
                            && path.get(1).getTransportationType() == TransportationType.FLIGHT);
        } else if (path.size() == 3) {
            return path.get(0).getTransportationType() != TransportationType.FLIGHT &&
                    path.get(1).getTransportationType() == TransportationType.FLIGHT &&
                    path.get(2).getTransportationType() != TransportationType.FLIGHT;
        }

        return false;
    }

    private RouteSearchResponseDto convertToDto(List<Transportation> path) {
        RouteSearchResponseDto.RouteSearchResponseDtoBuilder builder = RouteSearchResponseDto.builder();

        if (path.size() == 1) {
            // F
            builder.flight(transportationMapper.toDto(path.get(0)));
        } else if (path.size() == 2) {
            if (path.get(0).getTransportationType() == TransportationType.FLIGHT) {
                // F -> T
                builder.flight(transportationMapper.toDto(path.get(0)));
                builder.afterFlight(transportationMapper.toDto(path.get(1)));
            } else {
                // T -> F
                builder.beforeFlight(transportationMapper.toDto(path.get(0)));
                builder.flight(transportationMapper.toDto(path.get(1)));
            }
        } else if (path.size() == 3) {
            // (T -> F -> T)
            builder.beforeFlight(transportationMapper.toDto(path.get(0)))
                    .flight(transportationMapper.toDto(path.get(1)))
                    .afterFlight(transportationMapper.toDto(path.get(2)));
        }

        return builder.build();
    }

}
