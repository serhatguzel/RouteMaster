package com.routemaster.RouteMaster.dto;

import com.routemaster.RouteMaster.enums.TransportationType;
import com.routemaster.RouteMaster.validation.ValidOperationDays;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransportationDto {

    private Long id;

    @NotNull(message = "Origin location cannot be null")
    private LocationDto origin;

    @NotNull(message = "Destination location cannot be null")
    private LocationDto destination;

    @NotNull(message = "Transportation type cannot be null")
    @Enumerated(EnumType.STRING)
    private TransportationType transportationType;

    @ValidOperationDays
    @NotEmpty(message = "Operating days cannot be empty")
    private Set<Integer> operationDays = new HashSet<>();
}
