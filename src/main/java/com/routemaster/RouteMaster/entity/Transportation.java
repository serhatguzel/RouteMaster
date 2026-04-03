package com.routemaster.RouteMaster.entity;

import com.routemaster.RouteMaster.enums.TransportationType;
import jakarta.persistence.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import com.routemaster.RouteMaster.validation.ValidOperationDays;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "transportations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transportation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Origin location cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_location_id", nullable = false)
    private Location origin;

    @NotNull(message = "Destination location cannot be null")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_location_id", nullable = false)
    private Location destination;

    @NotNull(message = "Transportation type cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "transportation_type", nullable = false)
    private TransportationType transportationType;

    @ValidOperationDays
    @ElementCollection
    @CollectionTable(name = "transportation_operation_days", joinColumns = @JoinColumn(name = "transportation_id"))
    @Column(name = "operation_day")
    @NotEmpty(message = "Operating days cannot be empty")
    @Builder.Default
    private Set<Integer> operationDays = new HashSet<>();

    public boolean isValidRoute() {
        if (origin == null || destination == null) {
            return false;
        }
        return !origin.getLocationCode().equals(destination.getLocationCode());
    }
}
