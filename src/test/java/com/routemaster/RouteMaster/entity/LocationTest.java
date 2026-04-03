package com.routemaster.RouteMaster.entity;

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

class LocationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private Location createValidLocation() {
        return Location.builder()
                .name("Istanbul Airport")
                .city("Istanbul")
                .country("Turkey")
                .locationCode("IST")
                .build();
    }

    @Test
    @DisplayName("Should create location with valid data")
    void shouldCreateLocationWithValidData() {
        Location location = createValidLocation();

        Set<ConstraintViolation<Location>> violations = validator.validate(location);

        assertTrue(violations.isEmpty(), "Should not have any violations with valid location");
        assertEquals("Istanbul Airport", location.getName());
        assertEquals("Istanbul", location.getCity());
        assertEquals("Turkey", location.getCountry());
        assertEquals("IST", location.getLocationCode());
    }

    @Nested
    @DisplayName("Name Field Validations")
    class NameValidation {

        @Test
        @DisplayName("Should fail validation when name is blank")
        void shouldFailWhenNameIsBlank() {
            Location location = createValidLocation();
            location.setName("");

            Set<ConstraintViolation<Location>> violations = validator.validate(location);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Name is required and cannot be empty")));
        }

        @Test
        @DisplayName("Should fail validation when name exceeds 100 characters")
        void shouldFailWhenNameExceeds100Characters() {
            Location location = createValidLocation();
            location.setName("A".repeat(101));

            Set<ConstraintViolation<Location>> violations = validator.validate(location);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().equals("Name must be under 100 characters")));
        }
    }

    @Nested
    @DisplayName("Country Field Validations")
    class CountryValidation {

        @Test
        @DisplayName("Should fail validation when country is blank")
        void shouldFailWhenCountryIsBlank() {
            Location location = createValidLocation();
            location.setCountry("");

            Set<ConstraintViolation<Location>> violations = validator.validate(location);

            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when country exceeds 50 characters")
        void shouldFailWhenCountryExceeds50Characters() {
            Location location = createValidLocation();
            location.setCountry("A".repeat(51));

            Set<ConstraintViolation<Location>> violations = validator.validate(location);

            assertFalse(violations.isEmpty());
        }
    }

    @Nested
    @DisplayName("City Field Validations")
    class CityValidation {

        @Test
        @DisplayName("Should fail validation when city is blank")
        void shouldFailWhenCityIsBlank() {
            Location location = createValidLocation();
            location.setCity("");

            Set<ConstraintViolation<Location>> violations = validator.validate(location);

            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when city exceeds 50 characters")
        void shouldFailWhenCityExceeds50Characters() {
            Location location = createValidLocation();
            location.setCity("A".repeat(51));

            Set<ConstraintViolation<Location>> violations = validator.validate(location);

            assertFalse(violations.isEmpty());
        }
    }

    @Nested
    @DisplayName("Location Code Field Validations")
    class LocationCodeValidation {

        @Test
        @DisplayName("Should fail validation when location code is blank")
        void shouldFailWhenLocationCodeIsBlank() {
            Location location = createValidLocation();
            location.setLocationCode("");

            Set<ConstraintViolation<Location>> violations = validator.validate(location);

            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when location code is shorter than 3 characters")
        void shouldFailWhenLocationCodeIsTooShort() {
            Location location = createValidLocation();
            location.setLocationCode("AB");

            Set<ConstraintViolation<Location>> violations = validator.validate(location);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().contains("Location code must be between 3 and 10")));
        }

        @Test
        @DisplayName("Should fail validation when location code exceeds 10 characters")
        void shouldFailWhenLocationCodeIsTooLong() {
            Location location = createValidLocation();
            location.setLocationCode("ABCDEFGHIJK");

            Set<ConstraintViolation<Location>> violations = validator.validate(location);

            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should fail validation when location code contains lowercase letters")
        void shouldFailWhenLocationCodeContainsLowercase() {
            Location location = createValidLocation();
            location.setLocationCode("ist");

            Set<ConstraintViolation<Location>> violations = validator.validate(location);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream()
                    .anyMatch(v -> v.getMessage().contains("alphanumeric and uppercase")));
        }

        @Test
        @DisplayName("Should fail validation when location code contains special characters")
        void shouldFailWhenLocationCodeContainsSpecialChars() {
            Location location = createValidLocation();
            location.setLocationCode("IS-T");

            Set<ConstraintViolation<Location>> violations = validator.validate(location);

            assertFalse(violations.isEmpty());
        }

        @Test
        @DisplayName("Should accept location code with numbers and uppercase letters (e.g., IST1)")
        void shouldAcceptLocationCodeWithNumbers() {
            Location location = createValidLocation();
            location.setLocationCode("IST1");

            Set<ConstraintViolation<Location>> violations = validator.validate(location);

            assertTrue(violations.isEmpty(), "Should accept uppercase letters and numbers");
        }
    }
}
