package com.tiagoamp.booksapi.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BookRequestTest {

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("When empty values, should return validation errors")
    public void testValidation_emptyValues() {
        var request = new BookRequest();
        Set<ConstraintViolation<BookRequest>> errors = validator.validate(request);
        assertFalse(errors.isEmpty(), "Should have validation errors");
        List<String> requiredFieds = List.of("title", "language");
        errors.stream().forEach(e -> {
            String property = e.getPropertyPath().toString();
            assertTrue(requiredFieds.contains(property), "Should have required field validation error");
        });
    }

    @Test
    @DisplayName("When invalid values, should return validation errors")
    public void testValidation_invalidValues() {
        var request = new BookRequest();
        request.setYearOfPublication(-1);
        request.setAuthors("");
        Set<ConstraintViolation<BookRequest>> errors = validator.validate(request);
        assertFalse(errors.isEmpty(), "Should have validation errors");
        assertTrue(errors.stream().anyMatch(e -> e.getPropertyPath().toString().equals("title")), "Should have title validation error");
        assertTrue(errors.stream().anyMatch(e -> e.getPropertyPath().toString().equals("language")), "Should have language validation error");
        assertTrue(errors.stream().anyMatch(e -> e.getPropertyPath().toString().equals("yearOfPublication")), "Should have title year error");
        assertTrue(errors.stream().anyMatch(e -> e.getPropertyPath().toString().equals("authors")), "Should have title authors error");
    }

    @Test
    @DisplayName("When valid values, should not return validation errors")
    public void testValidation_validValues() {
        var request = new BookRequest("Title","Latim",2022,"Author Name");
        Set<ConstraintViolation<BookRequest>> errors = validator.validate(request);
        assertTrue(errors.isEmpty(), "Should not have validation errors");
    }

}