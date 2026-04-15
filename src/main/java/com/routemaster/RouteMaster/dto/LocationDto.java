package com.routemaster.RouteMaster.dto;

import com.routemaster.RouteMaster.enums.LocationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record LocationDto(

        Long id,

        @NotBlank(message = "Name is required and cannot be empty")
        @Size(max = 100, message = "Name must be under 100 characters")
        String name,

        @NotBlank(message = "City is required")
        @Size(max = 50, message = "City name too long")
        String city,

        @NotBlank(message = "Country is required")
        @Size(max = 50, message = "Country name too long")
        String country,

        @NotBlank(message = "Location code is required")
        @Size(min = 3, max = 10, message = "Location code must be between 3 and 10 characters")
        @Pattern(regexp = "^[A-Z0-9]+$", message = "Location code must be alphanumeric and uppercase")
        String locationCode,

        @NotNull(message = "Location type is required")
        LocationType type

) implements Serializable {}
