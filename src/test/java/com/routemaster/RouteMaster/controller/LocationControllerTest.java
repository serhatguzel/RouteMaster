package com.routemaster.RouteMaster.controller;

import com.routemaster.RouteMaster.dto.LocationDto;
import com.routemaster.RouteMaster.service.LocationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationController.class)
public class LocationControllerTest {

    private final String END_POINT_PATH = "/api/v1/locations";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LocationService locationService;

    @Autowired
    private ObjectMapper objectMapper;

    private LocationDto validLocationDto;

    @BeforeEach
    void setUp() {
        validLocationDto = LocationDto.builder()
                .id(1L)
                .locationCode("SAW")
                .city("Istanbul")
                .country("Turkey")
                .name("IST Airport")
                .build();
    }

    private LocationDto createInvalidDto() {
        return LocationDto.builder().id(1L).locationCode("SAW").build();
    }

    @Test
    @DisplayName("A valid GET request should return an OK status code of 200.")
    void shouldReturn200WhenGetByIdLocation() throws Exception {
        // GIVEN
        when(locationService.getLocationById(any())).thenReturn(validLocationDto);

        // WHEN & THEN
        mockMvc.perform(get(END_POINT_PATH + "/{id}", validLocationDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLocationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(validLocationDto.getName()));
    }

    @Test
    @DisplayName("When a GET request is sent with a non-existent ID, a 404 NOT FOUND status code should be returned.")
    void shouldReturn404WhenLocationNotFoundById() throws Exception {
        Long notFoundId = 999L;
        when(locationService.getLocationById(notFoundId))
                .thenThrow(new EntityNotFoundException("Location not found with id: " + notFoundId));
        // WHEN & THEN
        mockMvc.perform(get(END_POINT_PATH + "/{id}", notFoundId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("A valid GET request list should return a 200 OK status code.")
    void shouldReturn200WhenGetAllLocations() throws Exception {
        // GIVEN
        when(locationService.getAllLocations()).thenReturn(List.of(validLocationDto, validLocationDto));

        // WHEN & THEN
        mockMvc.perform(get(END_POINT_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[1].locationCode").value("SAW"))
                .andExpect(jsonPath("$.size()").value(2));
        }

    @Test
    @DisplayName("When a POST request is sent with incomplete data, a 400 BAD REQUEST status code should be returned.")
    void shouldReturn400WhenLocationIsInvalid() throws Exception {

        // WHEN & THEN
        mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInvalidDto())))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("A valid POST request should return a 201 CREATED status code.")
    void shouldReturn201WhenCreatingLocation() throws Exception {

        when(locationService.createLocation(any())).thenReturn(validLocationDto);

        // WHEN & THEN
        mockMvc.perform(post(END_POINT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLocationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(validLocationDto.getName()));
    }

    @Test
    @DisplayName("When a valid PUT request is sent, it should update successfully and return a 200 OK status code.")
    void shouldReturn200WhenUpdatingLocation() throws Exception {
        // GIVEN
        when(locationService.updateLocation(any(), any())).thenReturn(validLocationDto);

        // WHEN & THEN
        mockMvc.perform(put(END_POINT_PATH + "/{id}" , validLocationDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLocationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locationCode").value(validLocationDto.getLocationCode()));
    }

    @Test
    @DisplayName("When a PUT request is made with incorrect data, a 404 NOT FOUND status code should be returned.")
    void shouldReturn404WhenLocationNotFoundForUpdate() throws Exception {
        // GIVEN
        when(locationService.updateLocation(any(),any()))
                .thenThrow(new EntityNotFoundException("Location not found with id: " + validLocationDto.getId()));

        // WHEN & THEN
        mockMvc.perform(put(END_POINT_PATH + "/{id}" , validLocationDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLocationDto)))
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
    void shouldReturn204WhenDeletingLocation() throws Exception {
        // GIVEN
        Long deleteId = 1L;

        // WHEN & THEN
        mockMvc.perform(delete(END_POINT_PATH + "/{id}", deleteId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("When a DELETE request is sent with a non-existent ID, a 404 NOT FOUND status code should be returned.")
    void shouldReturn404WhenDeletingNonExistingLocation() throws Exception {
        Long notFoundId = 999L;

        doThrow(new EntityNotFoundException("Location not found with id: " + notFoundId))
                .when(locationService).deleteLocation(notFoundId);

        // WHEN & THEN
        mockMvc.perform(delete(END_POINT_PATH + "/{id}", notFoundId))
                .andExpect(status().isNotFound());
    }
}
