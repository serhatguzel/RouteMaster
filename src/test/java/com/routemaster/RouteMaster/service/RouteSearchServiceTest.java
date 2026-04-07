package com.routemaster.RouteMaster.service;

import com.routemaster.RouteMaster.dto.RouteSearchRequestDto;
import com.routemaster.RouteMaster.dto.RouteSearchResponseDto;
import com.routemaster.RouteMaster.dto.TransportationDto;
import com.routemaster.RouteMaster.entity.Location;
import com.routemaster.RouteMaster.entity.Transportation;
import com.routemaster.RouteMaster.enums.TransportationType;
import com.routemaster.RouteMaster.mapper.TransportationMapper;
import com.routemaster.RouteMaster.repository.TransportationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RouteSearchServiceTest {

    @Mock
    private TransportationRepository transportationRepository;

    @Mock
    private TransportationMapper transportationMapper;

    @InjectMocks
    private RouteSearchService routeSearchService;

    private Location locA, locB, locC, locD;
    private Transportation flightAB, busBC, subwayCD, uberDA;

    @BeforeEach
    void setUp() {
        locA = createLocation(1L, "IST", "Istanbul");
        locB = createLocation(2L, "LHR", "London");
        locC = createLocation(3L, "JFK", "New York");
        locD = createLocation(4L, "CDG", "Paris");

        flightAB = createTransportation(101L, locA, locB, TransportationType.FLIGHT);
        busBC = createTransportation(102L, locB, locC, TransportationType.BUS);
        subwayCD = createTransportation(103L, locC, locD, TransportationType.SUBWAY);
        uberDA = createTransportation(104L, locD, locA, TransportationType.UBER);
    }

    @Test
    @DisplayName("Should find a direct flight route")
    void shouldFindDirectFlight() {
        // GIVEN
        RouteSearchRequestDto request = new RouteSearchRequestDto(1L, 2L, LocalDate.now());
        when(transportationRepository.findByOperationDaysContaining(anyInt()))
                .thenReturn(Collections.singletonList(flightAB));
        
        when(transportationMapper.toDto(any(Transportation.class))).thenAnswer(i -> {
            Transportation t = i.getArgument(0);
            return TransportationDto.builder().id(t.getId()).build();
        });

        // WHEN
        List<RouteSearchResponseDto> results = routeSearchService.searchRoutes(request);

        // THEN
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertNotNull(results.get(0).getFlight());
        assertNull(results.get(0).getBeforeFlight());
        assertNull(results.get(0).getAfterFlight());
    }

    @Test
    @DisplayName("Should find a route with transfer before flight (T -> F)")
    void shouldFindTransferBeforeFlight() {
        // GIVEN
        Location locX = createLocation(10L, "X", "Start");
        Transportation busXA = createTransportation(200L, locX, locA, TransportationType.BUS);
        
        RouteSearchRequestDto request = new RouteSearchRequestDto(10L, 2L, LocalDate.now());
        when(transportationRepository.findByOperationDaysContaining(anyInt()))
                .thenReturn(Arrays.asList(busXA, flightAB));

        when(transportationMapper.toDto(any(Transportation.class))).thenAnswer(i -> {
            Transportation t = i.getArgument(0);
            return TransportationDto.builder().id(t.getId()).build();
        });

        // WHEN
        List<RouteSearchResponseDto> results = routeSearchService.searchRoutes(request);

        // THEN
        assertEquals(1, results.size());
        assertNotNull(results.get(0).getBeforeFlight());
        assertNotNull(results.get(0).getFlight());
        assertEquals(200L, results.get(0).getBeforeFlight().getId());
        assertEquals(101L, results.get(0).getFlight().getId());
    }

    @Test
    @DisplayName("Should find a full route (T -> F -> T)")
    void shouldFindFullRoute() {
        // GIVEN
        Location locStart = createLocation(10L, "START", "Start");
        Location locEnd = createLocation(20L, "END", "End");
        Transportation busStartA = createTransportation(301L, locStart, locA, TransportationType.BUS);
        Transportation shuttleBEnd = createTransportation(302L, locB, locEnd, TransportationType.UBER);

        RouteSearchRequestDto request = new RouteSearchRequestDto(10L, 20L, LocalDate.now());
        when(transportationRepository.findByOperationDaysContaining(anyInt()))
                .thenReturn(Arrays.asList(busStartA, flightAB, shuttleBEnd));

        when(transportationMapper.toDto(any(Transportation.class))).thenAnswer(i -> {
            Transportation t = i.getArgument(0);
            return TransportationDto.builder().id(t.getId()).build();
        });

        // WHEN
        List<RouteSearchResponseDto> results = routeSearchService.searchRoutes(request);

        // THEN
        assertEquals(1, results.size());
        assertNotNull(results.get(0).getBeforeFlight());
        assertNotNull(results.get(0).getFlight());
        assertNotNull(results.get(0).getAfterFlight());
        assertEquals(301L, results.get(0).getBeforeFlight().getId());
        assertEquals(101L, results.get(0).getFlight().getId());
        assertEquals(302L, results.get(0).getAfterFlight().getId());
    }

    @Test
    @DisplayName("Should throw exception when origin and destination are the same")
    void shouldThrowExceptionWhenOriginAndDestinationSame() {
        RouteSearchRequestDto request = new RouteSearchRequestDto(1L, 1L, LocalDate.now());
        
        assertThrows(RuntimeException.class, () -> routeSearchService.searchRoutes(request));
    }

    @Test
    @DisplayName("Should exclude routes with multiple flights (F -> F)")
    void shouldExcludeRoutesWithMultipleFlights() {
        // GIVEN
        Transportation flightBC = createTransportation(105L, locB, locC, TransportationType.FLIGHT);
        RouteSearchRequestDto request = new RouteSearchRequestDto(1L, 3L, LocalDate.now());
        
        when(transportationRepository.findByOperationDaysContaining(anyInt()))
                .thenReturn(Arrays.asList(flightAB, flightBC));

        // WHEN
        List<RouteSearchResponseDto> results = routeSearchService.searchRoutes(request);

        // THEN
        assertTrue(results.isEmpty(), "Routes with multiple flights should be excluded");
    }

    // Helper Methods
    private Location createLocation(Long id, String code, String name) {
        Location loc = new Location();
        loc.setId(id);
        loc.setLocationCode(code);
        loc.setName(name);
        return loc;
    }

    private Transportation createTransportation(Long id, Location origin, Location destination, TransportationType type) {
        Transportation t = new Transportation();
        t.setId(id);
        t.setOrigin(origin);
        t.setDestination(destination);
        t.setTransportationType(type);
        t.setOperationDays(Set.of(1, 2, 3, 4, 5, 6, 7)); // Runs every day
        return t;
    }
}
