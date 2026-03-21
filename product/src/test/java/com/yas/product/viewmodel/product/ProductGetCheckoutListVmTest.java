package com.yas.product.viewmodel.product;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductGetCheckoutListVmTest {

    private static ProductCheckoutListVm makeItem(Long id, String name, String sku, Double price) {
        return new ProductCheckoutListVm(
                id, name, null, null, sku, null, null, price, null,
                null, null, null, null, null
        );
    }

    // --- record accessors ---

    @Test
    void record_ShouldStoreAllFields() {
        List<ProductCheckoutListVm> items = List.of(
                makeItem(1L, "Product A", "SKU-A", 100.0),
                makeItem(2L, "Product B", "SKU-B", 200.0)
        );

        ProductGetCheckoutListVm vm = new ProductGetCheckoutListVm(items, 0, 10, 50, 5, false);

        assertEquals(2, vm.productCheckoutListVms().size());
        assertEquals(0, vm.pageNo());
        assertEquals(10, vm.pageSize());
        assertEquals(50, vm.totalElements());
        assertEquals(5, vm.totalPages());
        assertFalse(vm.isLast());
    }

    // --- empty list ---

    @Test
    void record_WhenProductListIsEmpty_ShouldStoreEmptyList() {
        ProductGetCheckoutListVm vm = new ProductGetCheckoutListVm(
                Collections.emptyList(), 0, 20, 0, 0, true
        );

        assertNotNull(vm.productCheckoutListVms());
        assertTrue(vm.productCheckoutListVms().isEmpty());
        assertEquals(0, vm.totalElements());
        assertTrue(vm.isLast());
    }

    @Test
    void record_WhenProductListIsNull_ShouldStoreNullList() {
        ProductGetCheckoutListVm vm = new ProductGetCheckoutListVm(
                null, 0, 10, 0, 0, true
        );

        assertNull(vm.productCheckoutListVms());
    }

    // --- pagination fields ---

    @Test
    void record_WhenFirstPage_ShouldHaveIsLastFalse() {
        ProductGetCheckoutListVm vm = new ProductGetCheckoutListVm(
                List.of(makeItem(1L, "P", "S", 1.0)), 0, 10, 100, 10, false
        );

        assertFalse(vm.isLast());
        assertEquals(0, vm.pageNo());
    }

    @Test
    void record_WhenLastPage_ShouldHaveIsLastTrue() {
        ProductGetCheckoutListVm vm = new ProductGetCheckoutListVm(
                List.of(makeItem(1L, "P", "S", 1.0)), 9, 10, 100, 10, true
        );

        assertTrue(vm.isLast());
        assertEquals(9, vm.pageNo());
    }

    @Test
    void record_WhenSinglePage_ShouldSetTotalPagesToOne() {
        ProductGetCheckoutListVm vm = new ProductGetCheckoutListVm(
                List.of(makeItem(1L, "P", "S", 1.0)), 0, 10, 5, 1, true
        );

        assertEquals(1, vm.totalPages());
        assertTrue(vm.isLast());
    }

    // --- totalPages edge cases ---

    @Test
    void record_WhenTotalElementsLessThanPageSize_ShouldHaveOnePage() {
        ProductGetCheckoutListVm vm = new ProductGetCheckoutListVm(
                List.of(makeItem(1L, "P", "S", 1.0)), 0, 20, 3, 1, true
        );

        assertEquals(1, vm.totalPages());
        assertEquals(3, vm.totalElements());
    }

    @Test
    void record_WhenZeroElements_ShouldHaveZeroPages() {
        ProductGetCheckoutListVm vm = new ProductGetCheckoutListVm(
                Collections.emptyList(), 0, 10, 0, 0, true
        );

        assertEquals(0, vm.totalPages());
        assertEquals(0, vm.totalElements());
        assertEquals(0, vm.pageNo());
    }

    // --- item access in list ---

    @Test
    void record_WhenMultipleItemsInList_ShouldAllowIndexAccess() {
        List<ProductCheckoutListVm> items = List.of(
                makeItem(1L, "First", "SKU-1", 10.0),
                makeItem(2L, "Second", "SKU-2", 20.0),
                makeItem(3L, "Third", "SKU-3", 30.0)
        );

        ProductGetCheckoutListVm vm = new ProductGetCheckoutListVm(items, 0, 3, 3, 1, true);

        assertEquals(3, vm.productCheckoutListVms().size());
        assertEquals("First", vm.productCheckoutListVms().get(0).name());
        assertEquals("Second", vm.productCheckoutListVms().get(1).name());
        assertEquals("Third", vm.productCheckoutListVms().get(2).name());
        assertEquals("SKU-2", vm.productCheckoutListVms().get(1).sku());
        assertEquals(20.0, vm.productCheckoutListVms().get(1).price());
    }

    // --- equals / hashCode / toString ---

    @Test
    void equals_WhenSameValues_ShouldBeEqual() {
        ProductGetCheckoutListVm vm1 = new ProductGetCheckoutListVm(
                Collections.emptyList(), 0, 10, 0, 0, true
        );
        ProductGetCheckoutListVm vm2 = new ProductGetCheckoutListVm(
                Collections.emptyList(), 0, 10, 0, 0, true
        );

        assertEquals(vm1, vm2);
        assertEquals(vm1.hashCode(), vm2.hashCode());
    }

    @Test
    void equals_WhenDifferentValues_ShouldNotBeEqual() {
        ProductGetCheckoutListVm vm1 = new ProductGetCheckoutListVm(
                List.of(makeItem(1L, "P", "S", 1.0)), 0, 10, 1, 1, true
        );
        ProductGetCheckoutListVm vm2 = new ProductGetCheckoutListVm(
                List.of(makeItem(1L, "P", "S", 1.0)), 1, 10, 1, 1, true
        );

        assertNotEquals(vm1, vm2);
    }

    @Test
    void toString_ShouldContainAllFields() {
        ProductGetCheckoutListVm vm = new ProductGetCheckoutListVm(
                List.of(makeItem(1L, "Test", "SKU-TEST", 99.99)), 0, 10, 1, 1, false
        );

        String str = vm.toString();
        assertTrue(str.contains("Test"));
        assertTrue(str.contains("SKU-TEST"));
        assertTrue(str.contains("0"));
        assertTrue(str.contains("10"));
        assertTrue(str.contains("false"));
    }
}
