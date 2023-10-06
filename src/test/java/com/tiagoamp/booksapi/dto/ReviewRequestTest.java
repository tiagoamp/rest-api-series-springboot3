package com.tiagoamp.booksapi.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReviewRequestTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    @Test
    @DisplayName("When empty values, should return validation errors")
    public void testValidation_emptyValues() {
        var request = new ReviewRequest(null);
        Set<ConstraintViolation<ReviewRequest>> errors = validator.validate(request);
        assertFalse(errors.isEmpty(), "Should have validation errors");
        List<String> requiredFieds = List.of("review");
        errors.stream().forEach(e -> {
            String property = e.getPropertyPath().toString();
            assertTrue(requiredFieds.contains(property), "Should have required field validation error");
        });
    }

    @Test
    @DisplayName("When invalid values, should return validation errors")
    public void testValidation_invalidValues() {
        var request = new ReviewRequest("");
        Set<ConstraintViolation<ReviewRequest>> errors = validator.validate(request);
        assertFalse(errors.isEmpty(), "Should have validation errors");
        assertTrue(errors.stream().anyMatch(e -> e.getPropertyPath().toString().equals("review")), "Should have text validation error");
    }

    @Test
    @DisplayName("When valid values, should not return validation errors")
    public void testValidation_validValues() {
        var request = new ReviewRequest("Review Text");
        Set<ConstraintViolation<ReviewRequest>> errors = validator.validate(request);
        assertTrue(errors.isEmpty(), "Should not have validation errors");
    }

}