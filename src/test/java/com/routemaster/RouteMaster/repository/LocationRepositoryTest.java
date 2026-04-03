package com.routemaster.RouteMaster.repository;

import com.routemaster.RouteMaster.entity.Location;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
/** TODO silinecek */
@DataJpaTest(properties = "spring.liquibase.enabled=false")
public class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    @Test
    @DisplayName("The new location should be successfully saved.")
    void shouldSaveLocation() {
        // Given
        Location location = Location.builder()
                .name("Istanbul Airport")
                .city("Istanbul")
                .country("Turkey")
                .locationCode("IST")
                .build();

        // When
        Location savedLocation = locationRepository.save(location);

        // Then
        assertNotNull(savedLocation.getId());
        assertEquals("Istanbul Airport", savedLocation.getName());
        assertEquals("IST", savedLocation.getLocationCode());
    }

    @Test
    @DisplayName("Registration should fail if the name is empty.")
    void shouldFailWhenNameIsNull() {
        Location invalidLocation = Location.builder()
                .city("London")
                .locationCode("LHR")
                .build();

        // DataIntegrityViolationException veya ConstraintViolationException bekleriz
        assertThrows(Exception.class, () -> {
            locationRepository.saveAndFlush(invalidLocation);
        });
    }

    @Test
    @DisplayName("Two locations with the same code should not be registered.")
    void shouldFailWhenCodeIsNotUnique() {
        // Given
        Location loc1 = Location.builder().name("Loc 1").city("XYX").country("XYX").locationCode("XYZ").build();
        locationRepository.saveAndFlush(loc1);

        Location loc2 = Location.builder().name("Loc 2").city("XYX").country("XYX").locationCode("XYZ").build();

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () -> {
            locationRepository.saveAndFlush(loc2);
        });
    }

    @Test
    @DisplayName("It should retrieve the location according to the given code.")
    void shouldFindByCode() {
        // Given
        locationRepository.save(Location.builder().name("Istanbul Havalimani").city("Istanbul").country("Turkiye").locationCode("IST").build());

        // When
        Optional<Location> found = locationRepository.findByLocationCode("IST");

        // Then
        assertTrue(found.isPresent());
        assertEquals("Istanbul Havalimani", found.get().getName());
    }

    @Test
    @DisplayName("Information for an existing location should be able to be updated.")
    void shouldUpdateLocation() {
        // 1. Register
        Location location = locationRepository.save(Location.builder()
                .name("Old Name")
                .locationCode("OLD")
                .city("Istanbul")
                .country("Türkiye")
                .build());
        Long savedId = location.getId();

        // 2. Update Process
        location.setName("New Name");
        location.setLocationCode("NEW");
        locationRepository.saveAndFlush(location);

        // 3. Verification
        Optional<Location> updatedLocation = locationRepository.findById(savedId);
        assertTrue(updatedLocation.isPresent());
        assertEquals("New Name", updatedLocation.get().getName());
        assertEquals("NEW", updatedLocation.get().getLocationCode());
    }

    @Test
    @DisplayName("Information for an existing location should be able to be deleted.")
    void shouldDeleteLocation() {
        // 1. Register
        Location location = locationRepository.save(Location.builder()
                .name("Old Name")
                .locationCode("OLD")
                .city("Istanbul")
                .country("Türkiye")
                .build());
        Long locationId = location.getId();

        // 2. Delete Process
        locationRepository.deleteById(locationId);

        // 3. Verification
        Optional<Location> deletedLocation = locationRepository.findById(locationId);
        assertFalse(deletedLocation.isPresent());
    }
}
