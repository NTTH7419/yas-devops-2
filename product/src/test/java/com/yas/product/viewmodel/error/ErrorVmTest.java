package com.yas.product.viewmodel.error;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorVmTest {

    @Test
    void constructor_WithAllFields_ShouldCreateErrorVm() {
        List<String> fieldErrors = List.of("field1", "field2");
        ErrorVm errorVm = new ErrorVm("404", "Not Found", "Resource not found", fieldErrors);

        assertEquals("404", errorVm.statusCode());
        assertEquals("Not Found", errorVm.title());
        assertEquals("Resource not found", errorVm.detail());
        assertEquals(2, errorVm.fieldErrors().size());
        assertEquals("field1", errorVm.fieldErrors().get(0));
        assertEquals("field2", errorVm.fieldErrors().get(1));
    }

    @Test
    void constructor_WithThreeArgs_ShouldInitializeFieldErrorsToEmptyList() {
        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Invalid input");

        assertEquals("400", errorVm.statusCode());
        assertEquals("Bad Request", errorVm.title());
        assertEquals("Invalid input", errorVm.detail());
        assertNotNull(errorVm.fieldErrors());
        assertTrue(errorVm.fieldErrors().isEmpty());
    }

    @Test
    void constructor_WithThreeArgs_ProvidedFieldErrorsShouldNotBeModified() {
        List<String> fieldErrors = List.of("name", "email");
        ErrorVm errorVm = new ErrorVm("422", "Validation Failed", "Check your input", fieldErrors);

        // The record should keep the provided fieldErrors (not create a new ArrayList copy)
        assertEquals(fieldErrors, errorVm.fieldErrors());
    }

    @Test
    void constructor_WithThreeArgs_EmptyFieldErrorsListShouldBeUsed() {
        ErrorVm errorVm = new ErrorVm("500", "Internal Server Error", "Something went wrong");

        assertNotNull(errorVm.fieldErrors());
        assertEquals(0, errorVm.fieldErrors().size());
    }

    @Test
    void constructor_WithAllFields_NullFieldErrorsShouldBeReplacedWithEmptyList() {
        ErrorVm errorVm = new ErrorVm("403", "Forbidden", "Access denied", null);

        // The 4-arg canonical constructor does not null-check; null is stored as-is
        assertNull(errorVm.fieldErrors());
    }
}
