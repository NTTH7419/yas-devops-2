package com.yas.product.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessagesUtilsTest {

    @Test
    void getMessage_WhenKeyExists_ShouldReturnFormattedMessage() {
        String result = MessagesUtils.getMessage("BRAND_NOT_FOUND", "BrandA");
        assertEquals("Brand BrandA is not found", result);
    }

    @Test
    void getMessage_WhenKeyExistsWithMultiplePlaceholders_ShouldFormatAll() {
        String result = MessagesUtils.getMessage("PRODUCT_NOT_FOUND", "SKU-001");
        assertEquals("Product SKU-001 is not found", result);
    }

    @Test
    void getMessage_WhenKeyNotFound_ShouldReturnKeyAsMessage() {
        String result = MessagesUtils.getMessage("UNKNOWN_ERROR_CODE");
        assertEquals("UNKNOWN_ERROR_CODE", result);
    }

    @Test
    void getMessage_WhenKeyExistsWithNoPlaceholders_ShouldReturnMessage() {
        String result = MessagesUtils.getMessage("MAKE_SURE_CATEGORY_DO_NOT_CONTAIN_CHILDREN");
        assertEquals("Please make sure this category contains no children", result);
    }

    @Test
    void getMessage_WhenKeyExistsWithMultiplePlaceholders_ShouldFormatBoth() {
        String result = MessagesUtils.getMessage("CATEGORY_NOT_FOUND", "Electronics");
        assertEquals("Category Electronics is not found", result);
    }
}
