package com.routemaster.RouteMaster.service;

import com.routemaster.RouteMaster.dto.TransportationDto;
import com.routemaster.RouteMaster.entity.Location;
import com.routemaster.RouteMaster.entity.Transportation;
import com.routemaster.RouteMaster.enums.LocationType;
import com.routemaster.RouteMaster.enums.TransportationType;
import com.routemaster.RouteMaster.exception.InvalidRouteException;
import com.routemaster.RouteMaster.mapper.TransportationMapper;
import com.routemaster.RouteMaster.repository.LocationRepository;
import com.routemaster.RouteMaster.repository.TransportationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransportationService {

    private final TransportationRepository transportationRepository;
    private final LocationRepository locationRepository;
    private final TransportationMapper transportationMapper;

    @Cacheable(value = "transportations", key = "#id")
    public TransportationDto getTransportationById(Long id) {
        log.info("Getting Transportation -> Id: {}", id);
        Transportation entity = transportationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transportation not found with id: " + id));
        log.info("Transportation got retrieved Id: {}", id);
        return transportationMapper.toDto(entity);
    }

    @Cacheable(value = "transportations", key = "'all'")
    public List<TransportationDto> getAllTransportations() {
        log.info("Getting All Transportations");
        List<TransportationDto> transportationList = transportationRepository.findAllByOrderByOriginNameAsc().stream()
                .map(transportationMapper::toDto)
                .toList();
        log.info("All Transportations got retrieved (Total: {})", transportationList.size());
        return transportationList;
    }

    @Transactional
    @CacheEvict(value = { "transportations", "routes" }, allEntries = true)
    public TransportationDto createTransportation(TransportationDto transportationDto) {
        log.info("Adding New Transportation: {}", transportationDto);
        Transportation transportation = transportationMapper.toEntity(transportationDto);
        
        validateAndSetLocations(transportation, transportationDto);
        
        Transportation saved = transportationRepository.save(transportation);
        log.info("Added New Transportation: {}", transportationDto);
        return transportationMapper.toDto(saved);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "transportations", key = "#id"),
            @CacheEvict(value = "transportations", key = "'all'"),
            @CacheEvict(value = "routes", allEntries = true)
    })
    public TransportationDto updateTransportation(Long id, TransportationDto transportationDto) {
        log.info("Updating Transportation -> Id: {}", id);
        Transportation entity = transportationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transportation not found with id: " + id));

        transportationMapper.updateEntityFromDto(transportationDto, entity);
        
        validateAndSetLocations(entity, transportationDto);

        log.info("Updated Transportation -> Id: {}", id);
        return transportationMapper.toDto(transportationRepository.save(entity));
    }

    private void validateAndSetLocations(Transportation transportation, TransportationDto dto) {
        Location origin = locationRepository.findById(dto.origin().id())
                .orElseThrow(() -> new EntityNotFoundException("Origin location not found"));
        Location destination = locationRepository.findById(dto.destination().id())
                .orElseThrow(() -> new EntityNotFoundException("Destination location not found"));

        transportation.setOrigin(origin);
        transportation.setDestination(destination);

        if (!transportation.isValidRoute()) {
            log.error("Origin and Destination locations cannot be the same.");
            throw new InvalidRouteException("Origin and Destination locations cannot be the same.");
        }

        if (transportation.getTransportationType() == TransportationType.FLIGHT) {
            if (origin.getType() != LocationType.AIRPORT || destination.getType() != LocationType.AIRPORT) {
                log.error("Flight can only be between AIRPORT locations. Origin: {}, Destination: {}",
                        origin.getType(), destination.getType());
                throw new InvalidRouteException("Flight transportations can only be between AIRPORT locations.");
            }
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "transportations", key = "#id"),
            @CacheEvict(value = "transportations", key = "'all'"),
            @CacheEvict(value = "routes", allEntries = true)
    })
    public void deleteTransportation(Long id) {
        log.warn("Deleting Transportation -> Id: {}", id);

        if (!transportationRepository.existsById(id)) {
            log.error("Location not found with id: {}", id);
            throw new EntityNotFoundException("Location not found with id: " + id);
        }

        transportationRepository.deleteById(id);

        log.warn("Deleted Transportation -> Id: {}", id);

    }
}
