package com.routemaster.RouteMaster.service;

import com.routemaster.RouteMaster.dto.TransportationDto;
import com.routemaster.RouteMaster.entity.Location;
import com.routemaster.RouteMaster.entity.Transportation;
import com.routemaster.RouteMaster.mapper.TransportationMapper;
import com.routemaster.RouteMaster.repository.LocationRepository;
import com.routemaster.RouteMaster.repository.TransportationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
        Transportation entity = transportationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transportation not found with id: " + id));
        return transportationMapper.toDto(entity);
    }

    public List<TransportationDto> getAllTransportations() {
        return transportationRepository.findAll().stream()
                .map(transportationMapper::toDto)
                .toList();
    }

    @Transactional
    public TransportationDto createTransportation(TransportationDto transportationDto) {

        Location origin = locationRepository.findById(transportationDto.getOrigin().getId())
                .orElseThrow(() -> new EntityNotFoundException("Origin location not found"));
        Location destination = locationRepository.findById(transportationDto.getDestination().getId())
                .orElseThrow(() -> new EntityNotFoundException("Destination location not found"));

        Transportation transportation = transportationMapper.toEntity(transportationDto);

        if(!transportation.isValidRoute()){
            throw new RuntimeException("Origin and Destination locations cannot be the same.");
        }

        transportation.setOrigin(origin);
        transportation.setDestination(destination);

        Transportation saved = transportationRepository.save(transportation);
        return transportationMapper.toDto(saved);

    }

    @Transactional
    public TransportationDto updateTransportation(Long id, TransportationDto transportationDto) {

        Transportation entity = transportationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transportation not found with id: " + id));

        transportationMapper.updateEntityFromDto(transportationDto, entity);

        return transportationMapper.toDto(transportationRepository.save(entity));
    }

    @Transactional
    public void deleteTransportation(Long id) {

        if (!transportationRepository.existsById(id)) {
            throw new EntityNotFoundException("Location not found with id: " + id);
        }

        transportationRepository.deleteById(id);
    }
}
