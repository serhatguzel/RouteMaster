package com.routemaster.RouteMaster.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;

@Builder
public record RouteSearchResponseDto(

    TransportationDto beforeFlight,

    @NotNull
    TransportationDto flight,

    TransportationDto afterFlight
) implements Serializable {}
