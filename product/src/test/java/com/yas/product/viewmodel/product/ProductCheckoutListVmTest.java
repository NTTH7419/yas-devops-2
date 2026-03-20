package com.yas.product.viewmodel.product;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ProductCheckoutListVmTest {

    private static ZonedDateTime dt(int year, int month, int day) {
        return ZonedDateTime.of(year, month, day, 0, 0, 0, 0, java.time.ZoneOffset.UTC);
    }

    // --- record accessors ---

    @Test
    void record_ShouldStoreAllFields() {
        ZonedDateTime created = dt(2024, 1, 15);
        ZonedDateTime modified = dt(2024, 6, 20);

        ProductCheckoutListVm vm = new ProductCheckoutListVm(
                42L, "Gaming Laptop", "High performance laptop", "Gaming laptop",
                "SKU-GAME-001", 10L, 5L, 1599.99, 3L,
                "https://cdn.example.com/laptop.jpg",
                created, "admin", modified, "editor"
        );

        assertEquals(42L, vm.id());
        assertEquals("Gaming Laptop", vm.name());
        assertEquals("High performance laptop", vm.description());
        assertEquals("Gaming laptop", vm.shortDescription());
        assertEquals("SKU-GAME-001", vm.sku());
        assertEquals(10L, vm.parentId());
        assertEquals(5L, vm.brandId());
        assertEquals(1599.99, vm.price());
        assertEquals(3L, vm.taxClassId());
        assertEquals("https://cdn.example.com/laptop.jpg", vm.thumbnailUrl());
        assertEquals(created, vm.createdOn());
        assertEquals("admin", vm.createdBy());
        assertEquals(modified, vm.lastModifiedOn());
        assertEquals("editor", vm.lastModifiedBy());
    }

    // --- null handling ---

    @Test
    void record_WhenAllOptionalFieldsAreNull_ShouldStoreNulls() {
        ProductCheckoutListVm vm = new ProductCheckoutListVm(
                null, null, null, null, null, null, null, null, null, null, null, null, null, null
        );

        assertNull(vm.id());
        assertNull(vm.name());
        assertNull(vm.description());
        assertNull(vm.shortDescription());
        assertNull(vm.sku());
        assertNull(vm.parentId());
        assertNull(vm.brandId());
        assertNull(vm.price());
        assertNull(vm.taxClassId());
        assertNull(vm.thumbnailUrl());
        assertNull(vm.createdOn());
        assertNull(vm.createdBy());
        assertNull(vm.lastModifiedOn());
        assertNull(vm.lastModifiedBy());
    }

    @Test
    void record_WhenParentIdIsNull_ShouldBeAllowed() {
        ProductCheckoutListVm vm = new ProductCheckoutListVm(
                1L, "Standalone Product", "desc", "short", "SKU-STAND",
                null, 2L, 99.99, 1L, null, null, null, null, null
        );

        assertNull(vm.parentId());
        assertEquals(2L, vm.brandId());
        assertEquals("SKU-STAND", vm.sku());
    }

    @Test
    void record_WhenBrandIdIsNull_ShouldBeAllowed() {
        ProductCheckoutListVm vm = new ProductCheckoutListVm(
                1L, "No Brand Product", "desc", "short", "SKU-NOBRAND",
                null, null, 49.99, 1L, null, null, null, null, null
        );

        assertNull(vm.brandId());
    }

    @Test
    void record_WhenPriceIsZero_ShouldBeStored() {
        ProductCheckoutListVm vm = new ProductCheckoutListVm(
                1L, "Free Item", "free desc", "free", "SKU-FREE",
                null, 1L, 0.0, 1L, null, null, null, null, null
        );

        assertEquals(0.0, vm.price());
    }

    // --- thumbnail URL ---

    @Test
    void record_WhenThumbnailUrlIsEmpty_ShouldStoreEmptyString() {
        ProductCheckoutListVm vm = new ProductCheckoutListVm(
                1L, "P", "d", "s", "SKU",
                null, 1L, 10.0, 1L, "", null, null, null, null
        );

        assertEquals("", vm.thumbnailUrl());
    }

    @Test
    void record_WhenThumbnailUrlIsFullUrl_ShouldStoreCorrectly() {
        String url = "https://cdn.example.com/images/products/123/thumbnail.png?v=abc";
        ProductCheckoutListVm vm = new ProductCheckoutListVm(
                1L, "P", "d", "s", "SKU",
                null, 1L, 10.0, 1L, url, null, null, null, null
        );

        assertEquals(url, vm.thumbnailUrl());
    }

    // --- fromModel static factory ---

    @Test
    void fromModel_ShouldBeAccessible() {
        // Verify the static factory method exists and is accessible via reflection
        assertDoesNotThrow(() ->
                ProductCheckoutListVm.class.getMethod("fromModel", com.yas.product.model.Product.class)
        );
    }

    // --- equals / hashCode / toString ---

    @Test
    void equals_WhenSameValues_ShouldBeEqual() {
        ProductCheckoutListVm vm1 = new ProductCheckoutListVm(
                1L, "Name", "desc", "short", "SKU", 10L, 5L, 100.0, 1L,
                "url", null, "a", null, "b"
        );
        ProductCheckoutListVm vm2 = new ProductCheckoutListVm(
                1L, "Name", "desc", "short", "SKU", 10L, 5L, 100.0, 1L,
                "url", null, "a", null, "b"
        );

        assertEquals(vm1, vm2);
        assertEquals(vm1.hashCode(), vm2.hashCode());
    }

    @Test
    void equals_WhenDifferentValues_ShouldNotBeEqual() {
        ProductCheckoutListVm vm1 = new ProductCheckoutListVm(
                1L, "Product A", "desc", "short", "SKU-A", null, 1L, 100.0, 1L,
                null, null, null, null, null
        );
        ProductCheckoutListVm vm2 = new ProductCheckoutListVm(
                2L, "Product B", "desc", "short", "SKU-B", null, 1L, 200.0, 1L,
                null, null, null, null, null
        );

        assertNotEquals(vm1, vm2);
    }

    @Test
    void toString_ShouldContainAllFields() {
        ProductCheckoutListVm vm = new ProductCheckoutListVm(
                1L, "Test Product", "desc", "short", "SKU-TEST", 5L, 2L, 50.0, 1L,
                "https://img.test", dt(2024, 3, 1), "user1", dt(2024, 4, 1), "user2"
        );

        String str = vm.toString();
        assertTrue(str.contains("Test Product"));
        assertTrue(str.contains("SKU-TEST"));
        assertTrue(str.contains("50.0"));
    }
}
