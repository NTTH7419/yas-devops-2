package com.yas.product.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private Category makeCategory(Long id, String name, String slug) {
        return new Category(id, name, "desc", slug, "meta", "meta-desc",
                (short) 1, true, 100L, null, null, null);
    }

    // --- equals ---

    @Test
    void equals_WhenSameId_ShouldBeEqual() {
        Category c1 = makeCategory(5L, "Electronics", "electronics");
        Category c2 = makeCategory(5L, "Gadgets", "gadgets"); // different name

        assertEquals(c1, c2);
    }

    @Test
    void equals_WhenDifferentId_ShouldNotBeEqual() {
        Category c1 = makeCategory(1L, "Cat", "cat");
        Category c2 = makeCategory(2L, "Cat", "cat");

        assertNotEquals(c1, c2);
    }

    @Test
    void equals_WhenBothIdAreNull_ShouldNotBeEqual() {
        Category c1 = makeCategory(null, "Cat", "cat");
        Category c2 = makeCategory(null, "Cat", "cat");

        assertNotEquals(c1, c2);
    }

    @Test
    void equals_WhenOneIdIsNull_ShouldNotBeEqual() {
        Category c1 = makeCategory(1L, "Cat", "cat");
        Category c2 = makeCategory(null, "Cat", "cat");

        assertNotEquals(c1, c2);
    }

    @Test
    void equals_WhenComparedToNull_ShouldReturnFalse() {
        Category cat = makeCategory(1L, "Cat", "cat");

        assertNotEquals(null, cat);
        assertNotEquals(cat, null);
    }

    @Test
    void equals_WhenComparedToDifferentClass_ShouldReturnFalse() {
        Category cat = makeCategory(1L, "Cat", "cat");

        assertNotEquals("Category", cat);
        assertNotEquals(cat, "Category");
    }

    @Test
    void equals_WhenComparedToSelf_ShouldReturnTrue() {
        Category cat = makeCategory(1L, "Cat", "cat");

        assertEquals(cat, cat);
    }

    // --- hashCode ---

    @Test
    void hashCode_WhenSameId_ShouldBeEqual() {
        Category c1 = makeCategory(5L, "Cat A", "cat-a");
        Category c2 = makeCategory(5L, "Cat B", "cat-b");

        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void hashCode_ShouldBeDeterministic() {
        Category cat = makeCategory(1L, "Cat", "cat");

        assertEquals(cat.hashCode(), cat.hashCode());
    }

    // --- parent relationship ---

    @Test
    void setParent_WhenParentCategory_ShouldSetCorrectly() {
        Category parent = makeCategory(1L, "Parent", "parent");
        Category child = makeCategory(2L, "Child", "child");
        child.setParent(parent);

        assertEquals(parent, child.getParent());
        assertEquals("Parent", child.getParent().getName());
    }

    @Test
    void getCategories_WhenChildrenSet_ShouldReturnChildren() {
        Category parent = makeCategory(1L, "Parent", "parent");
        Category child = makeCategory(2L, "Child", "child");
        parent.setCategories(List.of(child));

        assertEquals(1, parent.getCategories().size());
        assertEquals("Child", parent.getCategories().get(0).getName());
    }

    @Test
    void getCategories_ShouldNotBeNullByDefault() {
        Category cat = new Category();

        assertNotNull(cat.getCategories());
    }

    // --- field accessors ---

    @Test
    void allFields_ShouldBeSetCorrectly() {
        Category cat = new Category(10L, "Electronics", "Best electronics", "electronics",
                "elec,gadget", "Meta desc for electronics",
                (short) 5, true, 99L,
                null, null, null);

        assertEquals(10L, cat.getId());
        assertEquals("Electronics", cat.getName());
        assertEquals("Best electronics", cat.getDescription());
        assertEquals("electronics", cat.getSlug());
        assertEquals("elec,gadget", cat.getMetaKeyword());
        assertEquals("Meta desc for electronics", cat.getMetaDescription());
        assertEquals((short) 5, cat.getDisplayOrder());
        assertTrue(cat.getIsPublished());
        assertEquals(99L, cat.getImageId());
    }

    @Test
    void isPublished_WhenFalse_ShouldReturnFalse() {
        Category cat = new Category(1L, "Hidden", "desc", "hidden",
                null, null, (short) 0, false, null, null, null, null);

        assertFalse(cat.getIsPublished());
    }

    @Test
    void displayOrder_WhenZero_ShouldBeStored() {
        Category cat = makeCategory(1L, "Cat", "cat");
        cat.setDisplayOrder((short) 0);

        assertEquals((short) 0, cat.getDisplayOrder());
    }
}
