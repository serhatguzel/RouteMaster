package com.routemaster.RouteMaster.service;

import com.routemaster.RouteMaster.dto.LocationDto;
import com.routemaster.RouteMaster.entity.Location;
import com.routemaster.RouteMaster.exception.DuplicateLocationCodeException;
import com.routemaster.RouteMaster.exception.LocationInUseException;
import com.routemaster.RouteMaster.mapper.LocationMapper;
import com.routemaster.RouteMaster.repository.LocationRepository;
import java.util.List;

import com.routemaster.RouteMaster.repository.TransportationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LocationService {

    private final LocationRepository locationRepository;

    private final TransportationRepository transportationRepository;

    private final LocationMapper locationMapper;

    public LocationService(LocationRepository locationRepository, TransportationRepository transportationRepository,
            LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.transportationRepository = transportationRepository;
        this.locationMapper = locationMapper;
    }

    @Cacheable(value = "locations", key = "#id")
    public LocationDto getLocationById(Long id) {
        log.info("Getting Location -> Id: {}", id);

        Location entity = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found with id: " + id));

        log.info("Location id {} got", id);

        return locationMapper.toDto(entity);
    }

    @Cacheable(value = "locations", key = "'all'")
    public List<LocationDto> getAllLocations() {
        log.info("Getting All Locations");

        List<LocationDto> locationList = locationRepository.findAllByOrderByCityAscNameAsc().stream()
                .map(locationMapper::toDto)
                .toList();
        log.info("All locations got (Total: {})", locationList.size());
        return locationList;
    }

    @Transactional
    @CacheEvict(value = "locations", allEntries = true)
    public LocationDto createLocation(LocationDto locationDto) {
        log.info("Adding New Location: {}", locationDto);
        if (locationRepository.existsByLocationCode(locationDto.getLocationCode())) {
            log.error("Location code already exists: {}", locationDto.getLocationCode());
            throw new DuplicateLocationCodeException(locationDto.getLocationCode());
        }

        Location location = locationMapper.toEntity(locationDto);

        Location savedLocation = locationRepository.save(location);
        log.info("Added New Location: {}", locationDto);
        return locationMapper.toDto(savedLocation);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "locations", key = "#id"),
            @CacheEvict(value = "locations", key = "'all'"),
            @CacheEvict(value = "routes", allEntries = true)
    })
    public LocationDto updateLocation(Long id, LocationDto locationDto) {
        log.info("Updating Location -> Id: {}", id);
        Location entity = locationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Location not found with id: " + id));

        locationMapper.updateEntityFromDto(locationDto, entity);

        log.info("Updated Location -> Id: {}", id);

        return locationMapper.toDto(locationRepository.save(entity));
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "locations", key = "#id"),
            @CacheEvict(value = "locations", key = "'all'"),
            @CacheEvict(value = "routes", allEntries = true)
    })
    public void deleteLocation(Long id) {
        log.warn("Deleting Location -> Id: {}", id);
        if (!locationRepository.existsById(id)) {
            log.error("Location not found with id: {}", id);
            throw new EntityNotFoundException("Location not found with id: " + id);
        }

        if (transportationRepository.existsByOriginIdOrDestinationId(id, id)) {
            log.error("This location is in use by a transportation and cannot be deleted.");
            throw new LocationInUseException("This location is in use by a transportation and cannot be deleted.");
        }

        log.info("Deleted Location -> Id: {}", id);
        locationRepository.deleteById(id);
    }
}
