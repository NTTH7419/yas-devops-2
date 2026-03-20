package com.yas.product.model;

import com.yas.product.model.enumeration.DimensionUnit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DimensionUnitTest {

    // --- values ---

    @Test
    void values_ShouldReturnAllUnits() {
        DimensionUnit[] units = DimensionUnit.values();

        assertEquals(2, units.length);
    }

    @Test
    void values_ShouldContainCm() {
        assertNotNull(DimensionUnit.CM);
    }

    @Test
    void values_ShouldContainInch() {
        assertNotNull(DimensionUnit.INCH);
    }

    // --- getName ---

    @Test
    void getName_Cm_ShouldReturnCm() {
        assertEquals("cm", DimensionUnit.CM.getName());
    }

    @Test
    void getName_Inch_ShouldReturnInch() {
        assertEquals("inch", DimensionUnit.INCH.getName());
    }

    // --- valueOf ---

    @Test
    void valueOf_CM_ShouldReturnCm() {
        assertEquals(DimensionUnit.CM, DimensionUnit.valueOf("CM"));
    }

    @Test
    void valueOf_INCH_ShouldReturnInch() {
        assertEquals(DimensionUnit.INCH, DimensionUnit.valueOf("INCH"));
    }

    @Test
    void valueOf_InvalidName_ShouldThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> DimensionUnit.valueOf("KM"));
    }

    // --- toString ---

    @Test
    void toString_ShouldReturnEnumName() {
        assertEquals("CM", DimensionUnit.CM.toString());
        assertEquals("INCH", DimensionUnit.INCH.toString());
    }

    // --- ordinal ---

    @Test
    void ordinal_Cm_ShouldBeZero() {
        assertEquals(0, DimensionUnit.CM.ordinal());
    }

    @Test
    void ordinal_Inch_ShouldBeOne() {
        assertEquals(1, DimensionUnit.INCH.ordinal());
    }

    // --- name ---

    @Test
    void name_ShouldReturnEnumConstantName() {
        assertEquals("CM", DimensionUnit.CM.name());
        assertEquals("INCH", DimensionUnit.INCH.name());
    }
}
