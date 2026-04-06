package com.routemaster.RouteMaster.config;

import com.routemaster.RouteMaster.entity.Role;
import com.routemaster.RouteMaster.entity.User;
import com.routemaster.RouteMaster.entity.Location;
import com.routemaster.RouteMaster.enums.LocationType;
import com.routemaster.RouteMaster.enums.RoleType;
import com.routemaster.RouteMaster.entity.Transportation;
import com.routemaster.RouteMaster.enums.TransportationType;
import com.routemaster.RouteMaster.repository.RoleRepository;
import com.routemaster.RouteMaster.repository.UserRepository;
import com.routemaster.RouteMaster.repository.LocationRepository;
import com.routemaster.RouteMaster.repository.TransportationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LocationRepository locationRepository;
    private final TransportationRepository transportationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
            RoleRepository roleRepository,
            LocationRepository locationRepository,
            TransportationRepository transportationRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.locationRepository = locationRepository;
        this.transportationRepository = transportationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {

        if (roleRepository.count() > 0) {
            return;
        }

        Role adminRole = new Role();
        adminRole.setName(RoleType.ROLE_ADMIN.name());
        roleRepository.save(adminRole);

        Role agencyRole = new Role();
        agencyRole.setName(RoleType.ROLE_AGENCY.name());
        roleRepository.save(agencyRole);

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRoles(new HashSet<>(Set.of(adminRole)));
        userRepository.save(admin);

        User agency = new User();
        agency.setUsername("agency");
        agency.setPassword(passwordEncoder.encode("agency123"));
        agency.setRoles(new HashSet<>(Set.of(agencyRole)));
        userRepository.save(agency);

        // --- Başlangıç Konum Verileri ---
        Location ist = createLocation("Istanbul Airport", "Istanbul", "Turkey", "IST", LocationType.AIRPORT);
        Location saw = createLocation("Sabiha Gokcen Airport", "Istanbul", "Turkey", "SAW", LocationType.AIRPORT);
        Location jfk = createLocation("JFK International", "New York", "USA", "JFK", LocationType.AIRPORT);
        Location lhr = createLocation("London Heathrow", "London", "UK", "LHR", LocationType.AIRPORT);
        Location dxb = createLocation("Dubai International", "Dubai", "UAE", "DXB", LocationType.AIRPORT);
        Location center = createLocation("Istanbul City Center", "Istanbul", "Turkey", "CCIST", LocationType.OTHER);
        Location busStation = createLocation("Esenler Bus Station", "Istanbul", "Turkey", "BSIST", LocationType.OTHER);

        // --- Başlangıç Sefer Verileri (Transportations) ---
        if (transportationRepository.count() == 0) {
            // IST <-> SAW (Uçuş - Her Gün)
            saveTransportation(ist, saw, TransportationType.FLIGHT, Set.of(1, 2, 3, 4, 5, 6, 7));
            saveTransportation(saw, ist, TransportationType.FLIGHT, Set.of(1, 2, 3, 4, 5, 6, 7));

            // IST -> JFK (Uçuş - Pzt, Çar, Cum)
            saveTransportation(ist, jfk, TransportationType.FLIGHT, Set.of(1, 3, 5));

            // IST -> LHR (Uçuş - Sal, Per, Cmt)
            saveTransportation(ist, lhr, TransportationType.FLIGHT, Set.of(2, 4, 6));

            // SAW -> DXB (Uçuş - Hafta Sonu)
            saveTransportation(saw, dxb, TransportationType.FLIGHT, Set.of(6, 7));

            // Şehir içi aktarmalar
            saveTransportation(busStation, ist, TransportationType.BUS, Set.of(1, 2, 3, 4, 5));
            saveTransportation(center, saw, TransportationType.SUBWAY, Set.of(1, 2, 3, 4, 5, 6, 7));
            saveTransportation(ist, center, TransportationType.UBER, Set.of(1, 2, 3, 4, 5, 6, 7));
        }

        System.out.println("🚀 ROUTEMASTER: BAŞLANGIÇ VERİLERİ (ROLES, USERS, LOCATIONS & TRANSPORTATIONS) YÜKLENDİ!");
    }

    private Location createLocation(String name, String city, String country, String code, LocationType type) {
        return locationRepository.findByLocationCode(code)
                .orElseGet(() -> {
                    Location loc = new Location();
                    loc.setName(name);
                    loc.setCity(city);
                    loc.setCountry(country);
                    loc.setLocationCode(code);
                    loc.setType(type);
                    return locationRepository.save(loc);
                });
    }

    private void saveTransportation(Location origin, Location destination, TransportationType type, Set<Integer> days) {
        Transportation trans = new Transportation();
        trans.setOrigin(origin);
        trans.setDestination(destination);
        trans.setTransportationType(type);
        trans.setOperationDays(new HashSet<>(days));
        transportationRepository.save(trans);
    }
}
