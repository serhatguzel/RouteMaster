package com.routemaster.RouteMaster.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RouteSearchRequestDto {

    @NotNull(message = "Origin location ID is required")
    private Long originId;

    @NotNull(message = "Destination location ID is required")
    private Long destinationId;

    @NotNull(message = "Search date is required")
    @FutureOrPresent(message = "Search date cannot be in the past")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;
}
