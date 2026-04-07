package com.routemaster.RouteMaster.service;

import com.routemaster.RouteMaster.dto.LocationDto;
import com.routemaster.RouteMaster.entity.Location;
import com.routemaster.RouteMaster.repository.LocationRepository;
import com.routemaster.RouteMaster.repository.TransportationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mapstruct.factory.Mappers;
import com.routemaster.RouteMaster.mapper.LocationMapper;
import org.mockito.Spy;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private TransportationRepository transportationRepository;

    @Spy
    private LocationMapper locationMapper = Mappers.getMapper(LocationMapper.class);

    @InjectMocks
    private LocationService locationService;

    private LocationDto locationDto;
    private Location locationEntity;

    @BeforeEach
    void setUp() {
        locationDto = LocationDto.builder()
                .name("Istanbul Airport")
                .country("Turkey")
                .city("Istanbul")
                .locationCode("IST")
                .build();

        locationEntity = Location.builder()
                .id(1L)
                .name("Istanbul Airport")
                .country("Turkey")
                .city("Istanbul")
                .locationCode("IST")
                .build();
    }

    @Test
    @DisplayName("New location should been created succesfully")
    void shouldCreateLocationSuccessfully() {
        // Given
        when(locationRepository.existsByLocationCode(anyString())).thenReturn(false);
        when(locationRepository.save(any(Location.class))).thenReturn(locationEntity);

        // When
        LocationDto result = locationService.createLocation(locationDto);

        // Then
        assertNotNull(result);
        assertEquals("IST", result.getLocationCode());

        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    @DisplayName("It should throw an error if the same location code exists.")
    void shouldThrowExceptionWhenLocationCodeExists() {
        // Given
        when(locationRepository.existsByLocationCode("IST")).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> locationService.createLocation(locationDto));
        verify(locationRepository, never()).save(any());
    }

    @Test
    @DisplayName("The existing location must be updated successfully.")
    void shouldUpdateLocationSuccessfully() {
        // GIVEN
        Long targetId = 1L;
        Location existingEntity = Location.builder().id(targetId).name("Istanbul Havalimani").locationCode("IST").city("Istanbul").country("Turkey").build();
        LocationDto updateRequestDto = LocationDto.builder().name("Atatürk Havalimani").locationCode("IST").build();

        when(locationRepository.findById(targetId)).thenReturn(Optional.of(existingEntity));
        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        LocationDto result = locationService.updateLocation(targetId, updateRequestDto);

        // THEN
        assertNotNull(result);
        assertEquals(targetId, result.getId());
        assertEquals("Atatürk Havalimani", result.getName());

        verify(locationRepository, times(1)).save(any());

    }

    @Test
    @DisplayName("An error should be thrown when trying to update an ID that doesn't exist.")
    void shouldThrowExceptionWhenLocationNotFoundForUpdate() {
        // Given
        Long wrongId = 999L;
        when(locationRepository.findById(wrongId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () ->
                locationService.updateLocation(wrongId, locationDto)
        );

        verify(locationRepository, never()).save(any());
    }

    @Test
    @DisplayName("The existing location should be successfully deleted.")
    void shouldDeleteLocationSuccessfully() {
        // 1. GIVEN
        Long targetId = 1L;
        // Repository'de bu ID'nin var olduğunu simüle ediyoruz
        when(locationRepository.existsById(targetId)).thenReturn(true);
        // Lokasyonun kullanımda olmadığını simüle ediyoruz
        when(transportationRepository.existsByOriginIdOrDestinationId(targetId, targetId)).thenReturn(false);

        // WHEN
        locationService.deleteLocation(targetId);

        // THEN
        // Silme metodunun tam 1 kere çağrıldığını doğrula
        verify(locationRepository, times(1)).deleteById(targetId);
    }

    @Test
    @DisplayName("An error should be thrown when trying to delete an ID that doesn't exist.")
    void shouldThrowExceptionWhenLocationNotFoundForDelete() {
        // GIVEN
        Long wrongId = 99L;
        when(locationRepository.existsById(wrongId)).thenReturn(false);

        // WHEN & THEN
        assertThrows(EntityNotFoundException.class, () ->
                locationService.deleteLocation(wrongId)
        );

        verify(locationRepository, never()).deleteById(any());
    }
}
