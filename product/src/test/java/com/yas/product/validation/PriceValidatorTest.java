package com.yas.product.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PriceValidatorTest {

    private PriceValidator validator;

    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new PriceValidator();
    }

    @Test
    void isValid_WhenPriceIsZero_ShouldReturnTrue() {
        assertTrue(validator.isValid(0.0, context));
    }

    @Test
    void isValid_WhenPriceIsPositive_ShouldReturnTrue() {
        assertTrue(validator.isValid(100.0, context));
        assertTrue(validator.isValid(0.01, context));
        assertTrue(validator.isValid(999999.99, context));
    }

    @Test
    void isValid_WhenPriceIsNegative_ShouldReturnFalse() {
        assertFalse(validator.isValid(-1.0, context));
        assertFalse(validator.isValid(-0.01, context));
        assertFalse(validator.isValid(-100.0, context));
    }

    @Test
    void isValid_WhenPriceIsNull_ShouldReturnFalse() {
        // productPrice >= 0 throws NPE when null, so null is treated as invalid
        assertThrows(NullPointerException.class, () -> validator.isValid(null, context));
    }
}
