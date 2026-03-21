package com.yas.product.model;

import com.yas.product.model.enumeration.FilterExistInWhSelection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilterExistInWhSelectionTest {

    // --- values ---

    @Test
    void values_ShouldReturnAllOptions() {
        FilterExistInWhSelection[] values = FilterExistInWhSelection.values();

        assertEquals(3, values.length);
    }

    @Test
    void values_ShouldContainAll() {
        assertNotNull(FilterExistInWhSelection.ALL);
    }

    @Test
    void values_ShouldContainYes() {
        assertNotNull(FilterExistInWhSelection.YES);
    }

    @Test
    void values_ShouldContainNo() {
        assertNotNull(FilterExistInWhSelection.NO);
    }

    // --- valueOf ---

    @Test
    void valueOf_ALL_ShouldReturnAll() {
        assertEquals(FilterExistInWhSelection.ALL, FilterExistInWhSelection.valueOf("ALL"));
    }

    @Test
    void valueOf_YES_ShouldReturnYes() {
        assertEquals(FilterExistInWhSelection.YES, FilterExistInWhSelection.valueOf("YES"));
    }

    @Test
    void valueOf_NO_ShouldReturnNo() {
        assertEquals(FilterExistInWhSelection.NO, FilterExistInWhSelection.valueOf("NO"));
    }

    @Test
    void valueOf_InvalidName_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> FilterExistInWhSelection.valueOf("MAYBE"));
    }

    // --- toString ---

    @Test
    void toString_ShouldReturnEnumName() {
        assertEquals("ALL", FilterExistInWhSelection.ALL.toString());
        assertEquals("YES", FilterExistInWhSelection.YES.toString());
        assertEquals("NO", FilterExistInWhSelection.NO.toString());
    }

    // --- ordinal ---

    @Test
    void ordinal_ShouldBeInDeclarationOrder() {
        assertEquals(0, FilterExistInWhSelection.ALL.ordinal());
        assertEquals(1, FilterExistInWhSelection.YES.ordinal());
        assertEquals(2, FilterExistInWhSelection.NO.ordinal());
    }

    // --- name ---

    @Test
    void name_ShouldReturnEnumConstantName() {
        assertEquals("ALL", FilterExistInWhSelection.ALL.name());
        assertEquals("YES", FilterExistInWhSelection.YES.name());
        assertEquals("NO", FilterExistInWhSelection.NO.name());
    }
}
