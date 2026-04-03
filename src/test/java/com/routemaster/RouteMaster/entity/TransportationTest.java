package com.routemaster.RouteMaster.entity;

import com.routemaster.RouteMaster.enums.TransportationType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TransportationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Transportation createValidTransportation() {
        Transportation trans = new Transportation();
        trans.setTransportationType(TransportationType.FLIGHT);
        trans.setOrigin(Location.builder().locationCode("IST").build());
        trans.setDestination(Location.builder().locationCode("ESB").build());
        trans.getOperationDays().add(1); // 1 = Monday
        return trans;
    }

    @Test
    @DisplayName("Should maintain unique operation days using Set")
    void shouldMaintainUniqueOperationDays() {
        // Given
        Transportation transportation = new Transportation();

        // When - operationDays bir Set dönmeli
        transportation.getOperationDays().add(1); // MONDAY
        transportation.getOperationDays().add(3); // WEDNESDAY
        transportation.getOperationDays().add(1); // Tekrar MONDAY eklendi
        // Then
        assertEquals(2, transportation.getOperationDays().size(), "Set should not allow duplicate days");
        assertTrue(transportation.getOperationDays().contains(1));
        assertTrue(transportation.getOperationDays().contains(3));
    }

    @Test
    @DisplayName("Should assign TransportationType correctly")
    void shouldAssignTransportationType() {
        // Given
        Transportation transportation = new Transportation();
        // When
        transportation.setTransportationType(TransportationType.FLIGHT);
        // Then
        assertEquals(TransportationType.FLIGHT, transportation.getTransportationType());
    }

    @Test
    @DisplayName("Should assign origin and destination locations")
    void shouldAssignOriginAndDestination() {
        // Given
        Transportation transportation = new Transportation();
        Location origin = Location.builder().locationCode("IST").build();
        Location destination = Location.builder().locationCode("ESB").build();
        // When
        transportation.setOrigin(origin);
        transportation.setDestination(destination);
        // Then
        assertNotNull(transportation.getOrigin());
        assertNotNull(transportation.getDestination());
        assertEquals("IST", transportation.getOrigin().getLocationCode());
        assertEquals("ESB", transportation.getDestination().getLocationCode());
    }

    @Test
    @DisplayName("Should fail validation when origin and destination are the same")
    void shouldFailWhenOriginAndDestinationAreSame() {
        // Given
        Transportation transportation = new Transportation();
        Location origin = Location.builder().locationCode("IST").build();

        Location destination = Location.builder().locationCode("IST").build();
        transportation.setOrigin(origin);
        transportation.setDestination(destination);
        // When
        boolean isValid = transportation.isValidRoute();
        // Then
        assertFalse(isValid, "Origin and destination cannot be the same location");
    }

    @Nested
    @DisplayName("Transportation Validation Tests")
    class ValidationTests {
        @Test
        @DisplayName("Should create transportation with no validation errors when valid")
        void shouldPassValidationWithValidData() {
            Transportation trans = createValidTransportation();
            Set<ConstraintViolation<Transportation>> violations = validator.validate(trans);
            assertTrue(violations.isEmpty(), "Geçerli verilerde Hata olmamalı");
        }

        @Test
        @DisplayName("Should fail validation when transportation type is null")
        void shouldFailWhenTypeIsNull() {
            Transportation trans = createValidTransportation();
            trans.setTransportationType(null);
            Set<ConstraintViolation<Transportation>> violations = validator.validate(trans);
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when origin is null")
        void shouldFailWhenOriginIsNull() {
            Transportation trans = createValidTransportation();
            trans.setOrigin(null);
            Set<ConstraintViolation<Transportation>> violations = validator.validate(trans);
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when destination is null")
        void shouldFailWhenDestinationIsNull() {
            Transportation trans = createValidTransportation();
            trans.setDestination(null);
            Set<ConstraintViolation<Transportation>> violations = validator.validate(trans);
            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when operation day is less than 1")
        void shouldFailWhenOperationDayIsLessThanOne() {
            Transportation trans = createValidTransportation();
            trans.getOperationDays().add(0);

            Set<ConstraintViolation<Transportation>> violations = validator.validate(trans);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().contains("Operating days must be between 1 and 7")));
        }

        @Test
        @DisplayName("Should fail validation when operation day is greater than 7")
        void shouldFailWhenOperationDayIsGreaterThanSeven() {
            Transportation trans = createValidTransportation();
            trans.getOperationDays().add(8);

            Set<ConstraintViolation<Transportation>> violations = validator.validate(trans);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().contains("Operating days must be between 1 and 7")));
        }

        @Test
        @DisplayName("Should fail validation when operation days set is empty")
        void shouldFailWhenOperationDaysIsEmpty() {
            Transportation trans = createValidTransportation();
            trans.getOperationDays().clear();

            Set<ConstraintViolation<Transportation>> violations = validator.validate(trans);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().contains("Operating days cannot be empty")));
        }
    }
}
