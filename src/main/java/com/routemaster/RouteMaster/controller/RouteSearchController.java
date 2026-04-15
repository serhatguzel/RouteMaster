package com.routemaster.RouteMaster.controller;

import com.routemaster.RouteMaster.dto.RouteSearchRequestDto;
import com.routemaster.RouteMaster.dto.RouteSearchResponseDto;
import com.routemaster.RouteMaster.service.RouteSearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class RouteSearchController {

    private final RouteSearchService routeSearchService;

    @GetMapping("/search")
    public ResponseEntity<List<RouteSearchResponseDto>> searchRoute(@Valid RouteSearchRequestDto request) {
        List<RouteSearchResponseDto> routes = routeSearchService.searchRoutes(
                request.originId(), request.destinationId(), request.date());
        return ResponseEntity.ok(routes);
    }
}
