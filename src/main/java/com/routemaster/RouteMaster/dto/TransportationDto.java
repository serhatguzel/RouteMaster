package com.routemaster.RouteMaster.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.routemaster.RouteMaster.enums.TransportationType;
import com.routemaster.RouteMaster.validation.ValidOperationDays;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;

@Builder
public record TransportationDto (
        Long id,

        @NotNull(message = "Origin location cannot be null")
        LocationDto origin,

        @NotNull(message = "Destination location cannot be null")
        LocationDto destination,

        @NotNull(message = "Transportation type cannot be null")
        TransportationType transportationType,

        @ValidOperationDays
        @NotEmpty(message = "Operating days cannot be empty")
        Set<Integer> operationDays

) implements Serializable {

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        public List<String> operationDayNames() {
                if (operationDays == null) return List.of();
                return operationDays.stream()
                        .sorted()
                        .map(day -> DayOfWeek.of(day)
                                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                        .toList();
        }
}
