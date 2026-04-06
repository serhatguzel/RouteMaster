package com.routemaster.RouteMaster.service;

import com.routemaster.RouteMaster.dto.LocationDto;
import com.routemaster.RouteMaster.entity.Location;
import com.routemaster.RouteMaster.mapper.LocationMapper;
import com.routemaster.RouteMaster.repository.LocationRepository;
import java.util.List;

import com.routemaster.RouteMaster.repository.TransportationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    private final TransportationRepository transportationRepository;

    private final LocationMapper locationMapper;

    public LocationService(LocationRepository locationRepository, TransportationRepository transportationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.transportationRepository = transportationRepository;
        this.locationMapper = locationMapper;
    }

    public LocationDto getLocationById(Long id) {
        Location entity = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found with id: " + id));
        return locationMapper.toDto(entity);
    }

    public List<LocationDto> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(locationMapper::toDto)
                .toList();
    }

    @Transactional
    public LocationDto createLocation(LocationDto locationDto) {

        if (locationRepository.existsByLocationCode(locationDto.getLocationCode())) {
            throw new RuntimeException("Location code already exists: " + locationDto.getLocationCode());
        }

        Location location = locationMapper.toEntity(locationDto);

        Location savedLocation = locationRepository.save(location);
        return locationMapper.toDto(savedLocation);
    }

    @Transactional
    public LocationDto updateLocation(Long id, LocationDto locationDto) {

        Location entity = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found with id: " + id));

        locationMapper.updateEntityFromDto(locationDto, entity);

        return locationMapper.toDto(locationRepository.save(entity));
    }

    @Transactional
    public void deleteLocation(Long id) {

        if (!locationRepository.existsById(id)) {
            throw new EntityNotFoundException("Location not found with id: " + id);
        }

        if (transportationRepository.existsByOriginIdOrDestinationId(id, id)) {
            throw new RuntimeException("This location is in use by a transportation and cannot be deleted.");
        }

        locationRepository.deleteById(id);
    }
}
