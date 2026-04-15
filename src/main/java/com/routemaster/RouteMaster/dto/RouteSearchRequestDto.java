package com.routemaster.RouteMaster.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
public record RouteSearchRequestDto(

    @NotNull(message = "Origin location ID is required")
    Long originId,

    @NotNull(message = "Destination location ID is required")
    Long destinationId,

    @NotNull(message = "Search date is required")
    @FutureOrPresent(message = "Search date cannot be in the past")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    LocalDate date
){}
