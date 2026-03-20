package com.yas.product.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductOptionValueSaveVmTest {

    // --- anonymous implementation of the interface ---

    private static ProductOptionValueSaveVm makeVm(Long productOptionId) {
        return new ProductOptionValueSaveVm() {
            @Override
            public Long productOptionId() {
                return productOptionId;
            }
        };
    }

    // --- productOptionId accessor ---

    @Test
    void productOptionId_WhenProvided_ShouldReturnCorrectValue() {
        ProductOptionValueSaveVm vm = makeVm(42L);

        assertEquals(42L, vm.productOptionId());
    }

    @Test
    void productOptionId_WhenNull_ShouldReturnNull() {
        ProductOptionValueSaveVm vm = makeVm(null);

        assertNull(vm.productOptionId());
    }

    @Test
    void productOptionId_WhenZero_ShouldBeAccepted() {
        ProductOptionValueSaveVm vm = makeVm(0L);

        assertEquals(0L, vm.productOptionId());
    }

    // --- equals / hashCode ---

    @Test
    void equals_WhenSameInstance_ShouldBeEqual() {
        ProductOptionValueSaveVm vm = makeVm(5L);

        assertEquals(vm, vm);
        assertEquals(vm.hashCode(), vm.hashCode());
    }

    @Test
    void equals_WhenDifferentInstances_ShouldNotBeEqual() {
        // Anonymous classes are distinct instances
        ProductOptionValueSaveVm vm1 = makeVm(1L);
        ProductOptionValueSaveVm vm2 = makeVm(1L);

        assertNotEquals(vm1, vm2);
    }

    @Test
    void equals_WhenComparedToNull_ShouldReturnFalse() {
        ProductOptionValueSaveVm vm = makeVm(1L);

        assertNotEquals(null, vm);
    }

    @Test
    void equals_WhenComparedToDifferentType_ShouldReturnFalse() {
        ProductOptionValueSaveVm vm = makeVm(1L);

        assertNotEquals("not an interface", vm);
    }

    // --- toString ---

    @Test
    void toString_ShouldContainClassName() {
        ProductOptionValueSaveVm vm = makeVm(10L);

        String str = vm.toString();
        assertNotNull(str);
        assertTrue(str.contains("ProductOptionValueSaveVm") || str.contains("$$"));
    }
}
