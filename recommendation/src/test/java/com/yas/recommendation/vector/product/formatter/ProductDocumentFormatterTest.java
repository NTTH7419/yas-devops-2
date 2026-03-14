package com.yas.recommendation.vector.product.formatter;

import tools.jackson.databind.ObjectMapper;
import com.yas.recommendation.viewmodel.CategoryVm;
import com.yas.recommendation.viewmodel.ProductAttributeValueVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductDocumentFormatterTest {

    private ProductDocumentFormatter formatter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        formatter = new ProductDocumentFormatter();
        objectMapper = new ObjectMapper();
    }

    @Test
    void format_shouldReplacePlaceholdersAndRemoveHtml() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "Test Product");
        entityMap.put("description", "<p>Hello <b>World</b></p>");
        
        ProductAttributeValueVm attr1 = new ProductAttributeValueVm(1L, "Color", "Red");
        ProductAttributeValueVm attr2 = new ProductAttributeValueVm(2L, "Size", "Large");
        entityMap.put("attributeValues", Arrays.asList(attr1, attr2));
        
        CategoryVm cat1 = new CategoryVm(1L, "Electronics", null, null, null, null, null, null);
        entityMap.put("categories", Arrays.asList(cat1));

        String template = "Product: {name}, Description: {description}, Attributes: {attributeValues}, Categories: {categories}";
        
        String result = formatter.format(entityMap, template, objectMapper);
        
        String expected = "Product: Test Product, Description: Hello World, Attributes: [Color: Red, Size: Large], Categories: [Electronics]";
        assertEquals(expected, result);
    }

    @Test
    void format_shouldHandleNullAttributesAndCategories() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "Minimal Product");
        entityMap.put("attributeValues", null);
        entityMap.put("categories", null);

        String template = "{name} | {attributeValues} | {categories}";
        
        String result = formatter.format(entityMap, template, objectMapper);
        
        assertEquals("Minimal Product | [] | []", result);
    }
    
    @Test
    void format_shouldHandleMissingPlaceholders() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "Unused Name");
        
        String template = "Static Content";
        String result = formatter.format(entityMap, template, objectMapper);
        
        assertEquals("Static Content", result);
    }
}
