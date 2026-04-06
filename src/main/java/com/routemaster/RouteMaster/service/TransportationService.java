package com.routemaster.RouteMaster.service;

import com.routemaster.RouteMaster.dto.TransportationDto;
import com.routemaster.RouteMaster.entity.Location;
import com.routemaster.RouteMaster.entity.Transportation;
import com.routemaster.RouteMaster.mapper.TransportationMapper;
import com.routemaster.RouteMaster.repository.LocationRepository;
import com.routemaster.RouteMaster.repository.TransportationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TransportationService {

    private final TransportationRepository transportationRepository;
    private final LocationRepository locationRepository;
    private final TransportationMapper transportationMapper;

    public TransportationService(TransportationRepository transportationRepository, LocationRepository locationRepository, TransportationMapper transportationMapper) {
        this.transportationRepository = transportationRepository;
        this.locationRepository = locationRepository;
        this.transportationMapper = transportationMapper;
    }

    public TransportationDto getTransportationById(Long id) {
        log.info("Getting Transportation -> Id: {}", id);
        Transportation entity = transportationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transportation not found with id: " + id));
        log.info("Transportation got retrieved Id: {}" , id );
        return transportationMapper.toDto(entity);
    }

    public List<TransportationDto> getAllTransportations() {
        log.info("Getting All Transportations");
        List<TransportationDto> transportationList =  transportationRepository.findAll().stream()
                .map(transportationMapper::toDto)
                .toList();
        log.info("All Transportations got retrieved (Total: {})" , transportationList.size() );
        return transportationList;
    }

    @Transactional
    public TransportationDto createTransportation(TransportationDto transportationDto) {
        log.info("Adding New Transportation: {}", transportationDto);
        Location origin = locationRepository.findById(transportationDto.getOrigin().getId())
                .orElseThrow(() -> new EntityNotFoundException("Origin location not found"));
        Location destination = locationRepository.findById(transportationDto.getDestination().getId())
                .orElseThrow(() -> new EntityNotFoundException("Destination location not found"));

        Transportation transportation = transportationMapper.toEntity(transportationDto);

        transportation.setOrigin(origin);
        transportation.setDestination(destination);

        if(!transportation.isValidRoute()){
            log.error("Origin and Destination locations cannot be the same.");
            throw new RuntimeException("Origin and Destination locations cannot be the same.");
        }

        Transportation saved = transportationRepository.save(transportation);

        log.info("Added New Transportation: {}", transportationDto);
        return transportationMapper.toDto(saved);

    }

    @Transactional
    public TransportationDto updateTransportation(Long id, TransportationDto transportationDto) {
        log.info("Updating Transportation -> Id: {}", id);
        Transportation entity = transportationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transportation not found with id: " + id));

        transportationMapper.updateEntityFromDto(transportationDto, entity);

        log.info("Updated Transportation -> Id: {}", id);

        return transportationMapper.toDto(transportationRepository.save(entity));
    }

    @Transactional
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
