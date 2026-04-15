package com.routemaster.RouteMaster.entity;

import com.routemaster.RouteMaster.enums.LocationType;
import jakarta.persistence.*;
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

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false, length = 50)
    private String country;

    @Column(unique = true, length = 10, nullable = false)
    private String locationCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationType type;
}
