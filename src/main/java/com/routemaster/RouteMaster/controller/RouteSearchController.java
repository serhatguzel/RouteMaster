package com.routemaster.RouteMaster.controller;

import com.routemaster.RouteMaster.dto.RouteSearchResponseDto;
import com.routemaster.RouteMaster.service.RouteSearchService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
public class RouteSearchController {

    private final RouteSearchService routeSearchService;

    public RouteSearchController(RouteSearchService routeSearchService) {
        this.routeSearchService = routeSearchService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<RouteSearchResponseDto>> searchRoute(@Valid @RequestParam Long originId,
                                                                    @RequestParam Long destinationId,
                                                                    @RequestParam @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date) {
        List<RouteSearchResponseDto> routes = routeSearchService.searchRoutes(originId, destinationId, date);
        return ResponseEntity.ok(routes);
    }
}
