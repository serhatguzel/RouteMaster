package com.routemaster.RouteMaster.entity;

import com.routemaster.RouteMaster.enums.LocationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required and cannot be empty")
    @Size(max = 100, message = "Name must be under 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City name too long")
    @Column(nullable = false, length = 50)
    private String city;

    @NotBlank(message = "Country is required")
    @Size(max = 50, message = "Country name too long")
    @Column(nullable = false, length = 50)
    private String country;

    @NotBlank(message = "Location code is required")
    @Size(min = 3, max = 10, message = "Location code must be between 3 and 10 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Location code must be alphanumeric and uppercase")
    @Column(name = "location_code", unique = true, length = 10, nullable = false)
    private String locationCode;

    @NotNull(message = "Location type is required")
    @Enumerated(EnumType.STRING)
    private LocationType type;
}
