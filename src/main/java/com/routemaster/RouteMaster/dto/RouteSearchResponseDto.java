package com.routemaster.RouteMaster.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RouteSearchResponseDto implements Serializable {

    private TransportationDto beforeFlight;

    @NotNull
    private TransportationDto flight;

    private TransportationDto afterFlight;
}
