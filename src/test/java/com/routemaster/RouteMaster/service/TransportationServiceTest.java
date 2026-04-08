package com.routemaster.RouteMaster.service;

import com.routemaster.RouteMaster.dto.LocationDto;
import com.routemaster.RouteMaster.dto.TransportationDto;
import com.routemaster.RouteMaster.entity.Location;
import com.routemaster.RouteMaster.entity.Transportation;
import com.routemaster.RouteMaster.enums.LocationType;
import com.routemaster.RouteMaster.enums.TransportationType;
import com.routemaster.RouteMaster.mapper.TransportationMapper;
import com.routemaster.RouteMaster.repository.LocationRepository;
import com.routemaster.RouteMaster.repository.TransportationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransportationServiceTest {

    @Mock
    TransportationRepository transportationRepository;

    @Mock
    LocationRepository locationRepository;

    @InjectMocks
    TransportationService transportationService;

    @Spy
    private TransportationMapper transportationMapper = Mappers.getMapper(TransportationMapper.class);

    LocationDto originLocationDto;
    LocationDto destinationLocationDto;
    Location originLocation;
    Location destinationLocation;

    TransportationDto transportationDto;
    Transportation transportation;

    @BeforeEach
    void setUp() {

        originLocationDto = LocationDto.builder()
                .id(10L)
                .name("Istanbul Airport")
                .country("Turkey")
                .city("Istanbul")
                .locationCode("IST")
                .build();

        destinationLocationDto = LocationDto.builder()
                .id(20L)
                .name("London Airport")
                .country("England")
                .city("London")
                .locationCode("LHB")
                .build();

        originLocation= Location.builder()
                .id(10L)
                .name("Istanbul Airport")
                .country("Turkey")
                .city("Istanbul")
                .locationCode("IST")
                .type(LocationType.AIRPORT)
                .build();

        destinationLocation = Location.builder()
                .id(20L)
                .name("London Airport")
                .country("England")
                .city("London")
                .locationCode("LHB")
                .type(LocationType.AIRPORT)
                .build();

        transportationDto = TransportationDto.builder()
                .transportationType(TransportationType.FLIGHT)
                .origin(originLocationDto)
                .destination(destinationLocationDto)
                .operationDays(new HashSet<>(Set.of(1, 3, 5)))
                .build();

        transportation = Transportation.builder()
                .transportationType(TransportationType.FLIGHT)
                .origin(originLocation)
                .destination(destinationLocation)
                .operationDays(new HashSet<>(Set.of(1, 3, 5)))
                .build();
    }

    @Test
    @DisplayName("")
    void shouldGetTransportationByIdSuccessfully() {
        // Given
        when(transportationRepository.findById(any())).thenReturn(Optional.of(transportation));

        // When
        TransportationDto result = transportationService.getTransportationById(transportationDto.getId());

        // Then
        assertNotNull(result);
        assertEquals(TransportationType.FLIGHT, result.getTransportationType());

        verify(transportationRepository, times(1)).findById(transportationDto.getId());
    }

    @Test
    @DisplayName("")
    void shouldThrowExceptionWhenTransportationNotFoundById() {
        // Given
        when(transportationRepository.findById(any())).thenReturn(Optional.empty());

        // Then
        assertThrows(EntityNotFoundException.class, () -> {
            transportationService.getTransportationById(transportationDto.getId());
        });
    }

    @Test
    @DisplayName("Tüm ulasim listesi dogru boyutta cekilebilmeli")
    void shouldGetAllTransportations() {
        // Given
        when(transportationRepository.findAllByOrderByOriginNameAsc()).thenReturn(List.of(transportation, transportation));

        // When
        List<TransportationDto> resultList = transportationService.getAllTransportations();

        // Then
        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        verify(transportationRepository, times(1)).findAllByOrderByOriginNameAsc();

    }

    @Test
    @DisplayName("New transportation should been created succesfully")
    void shouldCreateTransportationSuccessfully() {
        // Given
        when(locationRepository.findById(originLocationDto.getId())).thenReturn(java.util.Optional.of(originLocation));
        when(locationRepository.findById(destinationLocationDto.getId())).thenReturn(java.util.Optional.of(destinationLocation));

        when(transportationRepository.save(any(Transportation.class))).thenReturn(transportation);

        // When
        TransportationDto result = transportationService.createTransportation(transportationDto);

        // Then
        assertNotNull(result);
        assertEquals(TransportationType.FLIGHT, result.getTransportationType());

        verify(transportationRepository, times(1)).save(any(Transportation.class));
    }

    @Test
    @DisplayName("If the origin point does not exist in the database, the record creation process should be " +
            "canceled and an error should be reported.")
    void shouldThrowExceptionWhenOriginNotFound() {
        // GIVEN
        when(locationRepository.findById(originLocationDto.getId())).thenReturn(Optional.empty());

        // WHEN & THEN
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            transportationService.createTransportation(transportationDto);
        });

        assertEquals("Origin location not found", exception.getMessage());

        verify(transportationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Origin ve Destination is same location")
    void shouldFailWhenOriginAndDestinationAreSame() {
        // GIVEN
        when(locationRepository.findById(anyLong())).thenReturn(Optional.of(originLocation));

        TransportationDto invalidDto = TransportationDto.builder()
                .origin(originLocationDto)
                .destination(originLocationDto)
                .transportationType(TransportationType.FLIGHT)
                .operationDays(Set.of(1))
                .build();

        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> {
            transportationService.createTransportation(invalidDto);
        });

        verify(transportationRepository, never()).save(any());
    }

    @Test
    @DisplayName("The existing transportation must be updated successfully.")
    void shouldUpdateTransportationSuccessfully() {

        // GIVEN
        Long targetId = 1L;

        when(transportationRepository.findById(targetId)).thenReturn(Optional.of(transportation));
        when(transportationRepository.save(any(Transportation.class))).thenReturn(transportation);

        TransportationDto updateRequestDto = TransportationDto.builder()
                .id(targetId)
                .transportationType(TransportationType.FLIGHT)
                .destination(destinationLocationDto)
                .origin(originLocationDto)
                .operationDays(new HashSet<>(Set.of(1,3,5,7))).build();

        TransportationDto result = transportationService.updateTransportation(targetId, updateRequestDto);

        assertNotNull(result);

        verify(transportationRepository, times(1)).save(any(Transportation.class));

    }

    @Test
    @DisplayName("An error should be thrown when trying to update an ID that doesn't exist.")
    void shouldThrowExceptionWhenTransportationNotFoundForUpdate() {
        // GIVEN
        when(transportationRepository.findById(any())).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(EntityNotFoundException.class, () -> {
            transportationService.updateTransportation(transportationDto.getId(), transportationDto);
        });

        verify(locationRepository, never()).findById(anyLong());
        verify(transportationRepository, never()).save(any());
    }

    @Test
    @DisplayName("The existing transportation should be successfully deleted.")
    void shouldDeleteTransportationSuccessfully() {
        // GIVEN
        when(transportationRepository.existsById(transportationDto.getId())).thenReturn(true);

        // WHEN
        transportationService.deleteTransportation(transportationDto.getId());

        // THEN
        verify(transportationRepository, times(1)).deleteById(transportationDto.getId());
    }

    @Test
    @DisplayName("An error should be thrown when trying to delete an ID that doesn't exist.")
    void shouldThrowExceptionWhenTransportationNotFoundForDelete() {
        // GIVEN
        when(transportationRepository.existsById(transportationDto.getId())).thenReturn(false);

        // WHEN & THEN
        assertThrows(EntityNotFoundException.class, () ->
                transportationService.deleteTransportation(transportationDto.getId())
        );

        verify(transportationRepository, never()).deleteById(any());
    }

}
