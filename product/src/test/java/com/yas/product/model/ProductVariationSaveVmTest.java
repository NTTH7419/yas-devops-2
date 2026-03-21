package com.yas.product.model;

import com.yas.product.viewmodel.product.ProductProperties;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProductVariationSaveVmTest {

    // --- anonymous implementation of the interface ---

    private static ProductVariationSaveVm makeVm(
            Double price,
            Long thumbnailMediaId,
            List<Long> productImageIds,
            Map<Long, String> optionValuesByOptionId,
            Long id,
            String sku,
            String gtin,
            String name,
            String slug) {
        return new ProductVariationSaveVm() {
            // price(), thumbnailMediaId(), productImageIds() inherited from ProductProperties
            @Override public Double price() { return price; }
            @Override public Long thumbnailMediaId() { return thumbnailMediaId; }
            @Override public List<Long> productImageIds() { return productImageIds; }
            // ProductProperties: id, name, slug, sku, gtin
            @Override public Long id() { return id; }
            @Override public String name() { return name; }
            @Override public String slug() { return slug; }
            @Override public String sku() { return sku; }
            @Override public String gtin() { return gtin; }
            // ProductVariationSaveVm own method
            @Override public Map<Long, String> optionValuesByOptionId() { return optionValuesByOptionId; }
        };
    }

    // --- price ---

    @Test
    void price_WhenPositive_ShouldReturnPrice() {
        ProductVariationSaveVm vm = makeVm(299.99, null, null, null, null, null, null, null, null);
        assertEquals(299.99, vm.price());
    }

    @Test
    void price_WhenZero_ShouldReturnZero() {
        ProductVariationSaveVm vm = makeVm(0.0, null, null, null, null, null, null, null, null);
        assertEquals(0.0, vm.price());
    }

    @Test
    void price_WhenNull_ShouldReturnNull() {
        ProductVariationSaveVm vm = makeVm(null, null, null, null, null, null, null, null, null);
        assertNull(vm.price());
    }

    // --- thumbnailMediaId ---

    @Test
    void thumbnailMediaId_WhenProvided_ShouldReturnId() {
        ProductVariationSaveVm vm = makeVm(null, 99L, null, null, null, null, null, null, null);
        assertEquals(99L, vm.thumbnailMediaId());
    }

    @Test
    void thumbnailMediaId_WhenNull_ShouldReturnNull() {
        ProductVariationSaveVm vm = makeVm(null, null, null, null, null, null, null, null, null);
        assertNull(vm.thumbnailMediaId());
    }

    // --- productImageIds ---

    @Test
    void productImageIds_WhenProvided_ShouldReturnList() {
        List<Long> ids = List.of(1L, 2L, 3L);
        ProductVariationSaveVm vm = makeVm(null, null, ids, null, null, null, null, null, null);

        assertEquals(3, vm.productImageIds().size());
        assertEquals(1L, vm.productImageIds().get(0));
    }

    @Test
    void productImageIds_WhenNull_ShouldReturnNull() {
        ProductVariationSaveVm vm = makeVm(null, null, null, null, null, null, null, null, null);
        assertNull(vm.productImageIds());
    }

    // --- optionValuesByOptionId ---

    @Test
    void optionValuesByOptionId_WhenProvided_ShouldReturnMap() {
        Map<Long, String> options = Map.of(10L, "Red", 20L, "Large");
        ProductVariationSaveVm vm = makeVm(null, null, null, options, null, null, null, null, null);

        assertEquals(2, vm.optionValuesByOptionId().size());
        assertEquals("Red", vm.optionValuesByOptionId().get(10L));
        assertEquals("Large", vm.optionValuesByOptionId().get(20L));
    }

    @Test
    void optionValuesByOptionId_WhenEmpty_ShouldReturnEmptyMap() {
        Map<Long, String> empty = Map.of();
        ProductVariationSaveVm vm = makeVm(null, null, null, empty, null, null, null, null, null);

        assertTrue(vm.optionValuesByOptionId().isEmpty());
    }

    @Test
    void optionValuesByOptionId_WhenNull_ShouldReturnNull() {
        ProductVariationSaveVm vm = makeVm(null, null, null, null, null, null, null, null, null);
        assertNull(vm.optionValuesByOptionId());
    }

    // --- ProductProperties accessors ---

    @Test
    void productProperties_WhenFullyPopulated_ShouldReturnAllValues() {
        ProductVariationSaveVm vm = makeVm(
                199.99, 5L, List.of(1L, 2L),
                Map.of(1L, "Option1"),
                100L, "SKU-VAR-001", "GTIN-VAR", "Variation Name", "variation-name"
        );

        assertEquals(100L, vm.id());           // from ProductProperties
        assertEquals("SKU-VAR-001", vm.sku());
        assertEquals("GTIN-VAR", vm.gtin());
        assertEquals("Variation Name", vm.name());
        assertEquals("variation-name", vm.slug());
    }

    @Test
    void productProperties_WhenAllNull_ShouldReturnAllNulls() {
        ProductVariationSaveVm vm = makeVm(null, null, null, null, null, null, null, null, null);

        assertNull(vm.id());
        assertNull(vm.sku());
        assertNull(vm.gtin());
        assertNull(vm.name());
        assertNull(vm.slug());
    }

    // --- equals / hashCode ---

    @Test
    void equals_WhenSameInstance_ShouldBeEqual() {
        ProductVariationSaveVm vm = makeVm(100.0, 1L, null, null, 1L, "SKU", "GTIN", "Name", "slug");
        assertEquals(vm, vm);
    }

    @Test
    void equals_WhenDifferentInstances_ShouldNotBeEqual() {
        ProductVariationSaveVm vm1 = makeVm(100.0, 1L, null, null, 1L, "SKU", "GTIN", "Name", "slug");
        ProductVariationSaveVm vm2 = makeVm(100.0, 1L, null, null, 1L, "SKU", "GTIN", "Name", "slug");
        assertNotEquals(vm1, vm2);
    }

    @Test
    void equals_WhenComparedToNull_ShouldReturnFalse() {
        ProductVariationSaveVm vm = makeVm(null, null, null, null, null, null, null, null, null);
        assertNotEquals(null, vm);
    }
}
