package com.routemaster.RouteMaster.controller;

import com.routemaster.RouteMaster.dto.TransportationDto;
import com.routemaster.RouteMaster.service.TransportationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transportations")
public class TransportationController {

    private final TransportationService transportationService;

    public TransportationController(TransportationService transportationService) {
        this.transportationService = transportationService;

    }

    @GetMapping
    public ResponseEntity<List<TransportationDto>> getAllTransportations() {
        return ResponseEntity.ok(transportationService.getAllTransportations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransportationDto> getTransportationById(@PathVariable Long id) {
        return ResponseEntity.ok(transportationService.getTransportationById(id));
    }

    @PostMapping
    public ResponseEntity<TransportationDto> createTransportation(@Valid @RequestBody TransportationDto transportationDto) {
        TransportationDto created =  transportationService.createTransportation(transportationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransportationDto> updateTransportation(@PathVariable Long id, @Valid @RequestBody TransportationDto transportationDto) {
        TransportationDto updated = transportationService.updateTransportation(id, transportationDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransportation(@PathVariable Long id) {
        transportationService.deleteTransportation(id);
        return ResponseEntity.noContent().build();
    }

}
