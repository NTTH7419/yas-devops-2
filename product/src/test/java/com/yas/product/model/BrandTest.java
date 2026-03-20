package com.yas.product.model;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class BrandTest {

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Class<?> cls = target.getClass();
            Field f = null;
            while (cls != null && f == null) {
                try {
                    f = cls.getDeclaredField(fieldName);
                } catch (NoSuchFieldException ignored) {
                    cls = cls.getSuperclass();
                }
            }
            if (f == null) throw new NoSuchFieldException(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Brand makeBrand(Long id, String name, String slug, boolean isPublished) {
        Brand brand = new Brand();
        brand.setId(id);
        brand.setName(name);
        brand.setSlug(slug);
        setField(brand, "isPublished", isPublished);
        return brand;
    }

    // --- equals ---

    @Test
    void equals_WhenSameId_ShouldBeEqual() {
        Brand b1 = makeBrand(10L, "Brand A", "brand-a", true);
        Brand b2 = makeBrand(10L, "Brand B", "brand-b", false);

        assertEquals(b1, b2);
    }

    @Test
    void equals_WhenDifferentId_ShouldNotBeEqual() {
        Brand b1 = makeBrand(1L, "Brand", "brand", true);
        Brand b2 = makeBrand(2L, "Brand", "brand", true);

        assertNotEquals(b1, b2);
    }

    @Test
    void equals_WhenBothIdAreNull_ShouldNotBeEqual() {
        Brand b1 = makeBrand(null, "Brand", "brand", true);
        Brand b2 = makeBrand(null, "Brand", "brand", false);

        assertNotEquals(b1, b2);
    }

    @Test
    void equals_WhenOneIdIsNull_ShouldNotBeEqual() {
        Brand b1 = makeBrand(1L, "Brand", "brand", true);
        Brand b2 = makeBrand(null, "Brand", "brand", true);

        assertNotEquals(b1, b2);
    }

    @Test
    void equals_WhenComparedToNull_ShouldReturnFalse() {
        Brand brand = makeBrand(1L, "Brand", "brand", true);

        assertNotEquals(null, brand);
        assertNotEquals(brand, null);
    }

    @Test
    void equals_WhenComparedToDifferentClass_ShouldReturnFalse() {
        Brand brand = makeBrand(1L, "Brand", "brand", true);

        assertNotEquals("Brand", brand);
        assertNotEquals(brand, "Brand");
    }

    @Test
    void equals_WhenComparedToSelf_ShouldReturnTrue() {
        Brand brand = makeBrand(1L, "Brand", "brand", true);

        assertEquals(brand, brand);
    }

    // --- hashCode ---

    @Test
    void hashCode_WhenSameId_ShouldBeEqual() {
        Brand b1 = makeBrand(10L, "Brand A", "brand-a", true);
        Brand b2 = makeBrand(10L, "Brand B", "brand-b", false);

        // Brand uses getClass().hashCode()
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    void hashCode_ShouldBeDeterministic() {
        Brand brand = makeBrand(1L, "Brand", "brand", true);

        int hash1 = brand.hashCode();
        int hash2 = brand.hashCode();

        assertEquals(hash1, hash2);
    }

    @Test
    void hashCode_ShouldNotThrowForNullId() {
        Brand brand = makeBrand(null, "Brand", "brand", true);

        assertDoesNotThrow(brand::hashCode);
    }

    // --- field accessors ---

    @Test
    void getters_ShouldReturnCorrectValues() {
        Brand brand = makeBrand(5L, "Apple", "apple", true);

        assertEquals(5L, brand.getId());
        assertEquals("Apple", brand.getName());
        assertEquals("apple", brand.getSlug());
        assertTrue(brand.isPublished());
    }

    @Test
    void setters_ShouldUpdateIdNameSlug() {
        Brand brand = new Brand();

        brand.setId(99L);
        brand.setName("Samsung");
        brand.setSlug("samsung");

        assertEquals(99L, brand.getId());
        assertEquals("Samsung", brand.getName());
        assertEquals("samsung", brand.getSlug());
    }

    @Test
    void isPublished_WhenFalse_ShouldReturnFalse() {
        Brand brand = makeBrand(1L, "Brand", "brand", false);

        assertFalse(brand.isPublished());
    }

    @Test
    void isPublished_WhenTrue_ShouldReturnTrue() {
        Brand brand = makeBrand(1L, "Brand", "brand", true);

        assertTrue(brand.isPublished());
    }

    @Test
    void productsList_ShouldBeSettable() {
        Brand brand = makeBrand(1L, "Brand", "brand", true);
        Product p1 = new Product();
        p1.setId(1L);
        Product p2 = new Product();
        p2.setId(2L);

        brand.setProducts(java.util.List.of(p1, p2));

        assertEquals(2, brand.getProducts().size());
    }
}
