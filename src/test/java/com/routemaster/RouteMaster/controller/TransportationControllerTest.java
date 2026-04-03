package com.routemaster.RouteMaster.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.routemaster.RouteMaster.dto.LocationDto;
import com.routemaster.RouteMaster.dto.TransportationDto;
import com.routemaster.RouteMaster.enums.TransportationType;
import com.routemaster.RouteMaster.service.LocationService;
import com.routemaster.RouteMaster.service.TransportationService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransportationController.class)
public class TransportationControllerTest {

    private final String END_POINT_PATH = "/api/v1/transportations";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransportationService transportationService;

    @MockitoBean
    private LocationService locationService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransportationDto validTransportationDto;

    private LocationDto originLocationDto;
    private LocationDto destinationLocationDto;

    @BeforeEach
    void setUp() {

        originLocationDto = LocationDto.builder()
                .id(1L)
                .name("Istanbul Airport")
                .country("Turkey")
                .city("Istanbul")
                .locationCode("IST")
                .build();

        destinationLocationDto = LocationDto.builder()
                .id(2L)
                .name("London Airport")
                .country("England")
                .city("London")
                .locationCode("LHB")
                .build();

        validTransportationDto = TransportationDto.builder()
                .id(1L)
                .transportationType(TransportationType.FLIGHT)
                .origin(originLocationDto)
                .destination(destinationLocationDto)
                .operationDays(new HashSet<>(Set.of(1, 3, 5)))
                .build();
    }

    @Test
    @DisplayName("A valid GET request should return an OK status code of 200.")
    void shouldReturn200WhenGetByIdTransportation() throws Exception {
        // GIVEN
        when(transportationService.getTransportationById(any())).thenReturn(validTransportationDto);

        // WHEN & THEN
        mockMvc.perform(get(END_POINT_PATH + "/{id}", validTransportationDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTransportationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transportationType").value(validTransportationDto.getTransportationType().name()));
    }

    @Test
    @DisplayName("When a GET request is sent with a non-existent ID, a 404 NOT FOUND status code should be returned.")
    void shouldReturn404WhenTransportationNotFoundById() throws Exception {
        Long notFoundId = 999L;
        when(transportationService.getTransportationById(notFoundId))
                .thenThrow(new EntityNotFoundException("Transportation not found with id: " + notFoundId));
        // WHEN & THEN
        mockMvc.perform(get(END_POINT_PATH + "/{id}", notFoundId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("A valid GET request list should return a 200 OK status code.")
    void shouldReturn200WhenGetAllTransportations() throws Exception {
        // GIVEN
        when(transportationService.getAllTransportations()).thenReturn(List.of(validTransportationDto, validTransportationDto));

        // WHEN & THEN
        mockMvc.perform(get(END_POINT_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[1].transportationType").value(TransportationType.FLIGHT.name()))
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    @DisplayName("When a POST request is sent with incomplete data, a 400 BAD REQUEST status code should be returned.")
    void shouldReturn400WhenTransportationIsInvalid() throws Exception {

        // WHEN & THEN
        mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInvalidDto())))
                .andExpect(status().isBadRequest());
    }

    private TransportationDto createInvalidDto() {
        return TransportationDto.builder().id(1L).transportationType(TransportationType.FLIGHT).build();
    }

    @Test
    @DisplayName("A valid POST request should return a 201 CREATED status code.")
    void shouldReturn201WhenCreatingTransportation() throws Exception {

        when(transportationService.createTransportation(any())).thenReturn(validTransportationDto);

        // WHEN & THEN
        mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTransportationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transportationType").value(validTransportationDto.getTransportationType().name()));
    }

    @Test
    @DisplayName("When a valid PUT request is sent, it should update successfully and return a 200 OK status code.")
    void shouldReturn200WhenUpdatingTransportation() throws Exception {
        // GIVEN
        when(transportationService.updateTransportation(any(), any())).thenReturn(validTransportationDto);

        // WHEN & THEN
        mockMvc.perform(put(END_POINT_PATH + "/{id}" , validTransportationDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTransportationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transportationType").value(validTransportationDto.getTransportationType().name()));
    }

    @Test
    @DisplayName("When a PUT request is made with incorrect data, a 404 NOT FOUND status code should be returned.")
    void shouldReturn404WhenTransportationNotFoundForUpdate() throws Exception {
        // GIVEN
        when(transportationService.updateTransportation(any(),any()))
                .thenThrow(new EntityNotFoundException("Location not found with id: " + validTransportationDto.getId()));

        // WHEN & THEN
        mockMvc.perform(put(END_POINT_PATH + "/{id}" , validTransportationDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validTransportationDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("When a PUT request is sent with missing data, a 400 BAD REQUEST status code should be returned.")
    void shouldReturn400WhenUpdateHasInvalidData() throws Exception {

        // WHEN & THEN
        mockMvc.perform(put(END_POINT_PATH + "/{id}" , createInvalidDto().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInvalidDto())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("A valid Delete request should return a 204 status code.")
    void shouldReturn204WhenDeletingTransportation() throws Exception {
        // GIVEN
        Long deleteId = 1L;

        // WHEN & THEN
        mockMvc.perform(delete(END_POINT_PATH + "/{id}", deleteId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("When a DELETE request is sent with a non-existent ID, a 404 NOT FOUND status code should be returned.")
    void shouldReturn404WhenDeletingNonExistingTransportation() throws Exception {
        Long notFoundId = 999L;

        doThrow(new EntityNotFoundException("Location not found with id: " + notFoundId))
                .when(transportationService).deleteTransportation(notFoundId);

        // WHEN & THEN
        mockMvc.perform(delete(END_POINT_PATH + "/{id}", notFoundId))
                .andExpect(status().isNotFound());
    }



}
