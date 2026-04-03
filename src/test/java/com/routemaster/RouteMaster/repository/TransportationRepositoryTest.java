package com.routemaster.RouteMaster.repository;

import com.routemaster.RouteMaster.entity.Location;
import com.routemaster.RouteMaster.entity.Transportation;
import com.routemaster.RouteMaster.enums.TransportationType;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/** TODO silinecek*/
@DataJpaTest(properties = "spring.liquibase.enabled=false")
public class TransportationRepositoryTest {

    @Autowired
    private TransportationRepository transportationRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    EntityManager entityManager;

    private Location istanbul;
    private Location london;

    @BeforeEach
    void setUp() {

        istanbul = locationRepository.save(Location.builder().name("Istanbul Havalimani").city("Istanbul").country("Turkiye").locationCode("IST").build());
        london = locationRepository.save(Location.builder().name("London Airport").city("London").country("England").locationCode("LHR").build());
    }

    @Test
    @DisplayName("Transportation records and operation dates must be accurately recorded.")
    void shouldSaveTransportationWithOperatingDays() {
        // Given
        Transportation flight = Transportation.builder()
                .origin(istanbul)
                .destination(london)
                .transportationType(TransportationType.FLIGHT)
                .operationDays(Set.of(1, 3, 5)) // Monday, Wednesday, Friday
                .build();

        // When
        Transportation saved = transportationRepository.save(flight);
        transportationRepository.flush();

        // Then
        assertNotNull(saved.getId());
        assertEquals(3, saved.getOperationDays().size());
        assertTrue(saved.getOperationDays().contains(1));
        assertEquals(TransportationType.FLIGHT, saved.getTransportationType());
    }

    @Test
    @DisplayName("It should be able to list transportation options based on the departure point.")
    void shouldFindByOrigin() {
        // Given
        transportationRepository.save(Transportation.builder()
                .origin(istanbul).destination(london).transportationType(TransportationType.FLIGHT).operationDays(Set.of(1,3,5)).build());

        // When
        List<Transportation> results = transportationRepository.findByOrigin(istanbul);

        // Then
        assertThat(results).hasSize(1);
        assertEquals(london.getLocationCode(), results.get(0).getDestination().getLocationCode());
    }

    @Test
    @DisplayName("An error should be thrown if a day greater than 7 is entered.")
    void shouldThrowExceptionForInvalidDay() {
        Transportation invalid = Transportation.builder()
                .origin(istanbul)
                .destination(london)
                .transportationType(TransportationType.FLIGHT)
                .operationDays(Set.of(9))
                .build();

        assertThrows(ConstraintViolationException.class, () -> {
            transportationRepository.saveAndFlush(invalid);
        });
    }

    @Test
    @DisplayName("Operation dates should be updateable.")
    void shouldUpdateOperatingDays() {
        Transportation transport = transportationRepository.save(Transportation.builder()
                .origin(istanbul).destination(london)
                .transportationType(TransportationType.FLIGHT)
                .operationDays(new HashSet<>(Set.of(1))).build());

        transport = transportationRepository.saveAndFlush(transport); // DB'ye zorla yaz

        // 2. Güncelleme
        transport.setOperationDays(new HashSet<>(Set.of(2))); // Salı yap
        transportationRepository.saveAndFlush(transport); // Tekrar zorla yaz

        entityManager.clear();

        Transportation updated = transportationRepository.findById(transport.getId()).get();

        assertEquals(1, updated.getOperationDays().size());
        assertTrue(updated.getOperationDays().contains(2));
        assertFalse(updated.getOperationDays().contains(1));
    }

    @Test
    @DisplayName("Transportation should be deletable.")
    void shouldDeleteTransportation() {
        Transportation transport = transportationRepository.save(Transportation.builder()
                .origin(istanbul).destination(london)
                .transportationType(TransportationType.FLIGHT)
                .operationDays(new HashSet<>(Set.of(1))).build());

        Long transportId = transport.getId();

        transportationRepository.deleteById(transportId);

        Optional<Transportation> transportation = transportationRepository.findById(transportId);

        assertFalse(transportation.isPresent());
    }
}
