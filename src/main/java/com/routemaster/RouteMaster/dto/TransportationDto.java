package com.routemaster.RouteMaster.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.routemaster.RouteMaster.enums.TransportationType;
import com.routemaster.RouteMaster.validation.ValidOperationDays;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.io.Serializable;
import java.time.format.TextStyle;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TransportationDto implements Serializable {

    private Long id;

    @NotNull(message = "Origin location cannot be null")
    private LocationDto origin;

    @NotNull(message = "Destination location cannot be null")
    private LocationDto destination;

    @NotNull(message = "Transportation type cannot be null")
    @Enumerated(EnumType.STRING)
    private TransportationType transportationType;

    @Builder.Default
    @ValidOperationDays
    @NotEmpty(message = "Operating days cannot be empty")
    private Set<Integer> operationDays = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public List<String> getOperationDayNames() {
        return operationDays.stream()
                .sorted()
                .map(day -> java.time.DayOfWeek.of(day)
                        .getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                .collect(Collectors.toList());
    }
}
