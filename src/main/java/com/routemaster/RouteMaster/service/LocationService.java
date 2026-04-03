package com.routemaster.RouteMaster.service;

import com.routemaster.RouteMaster.dto.LocationDto;
import com.routemaster.RouteMaster.entity.Location;
import com.routemaster.RouteMaster.mapper.LocationMapper;
import com.routemaster.RouteMaster.repository.LocationRepository;
import java.util.List;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    private final LocationMapper locationMapper;

    public LocationService(LocationRepository locationRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
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

        Location location = Location.builder()
                .name(locationDto.getName())
                .country(locationDto.getCountry())
                .city(locationDto.getCity())
                .locationCode(locationDto.getLocationCode().toUpperCase())
                .build();

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

        /** TODO düzenleme yapılacak */
        // 2. İlişkisel veri kontrolü (Opsiyonel ama profesyonel bir dokunuş)
        // Eğer bu lokasyon bir Transportation içinde kullanılıyorsa,
        // DB seviyesinde hata almamak için burada özel bir kontrol yapılabilir.

        locationRepository.deleteById(id);
    }
}
