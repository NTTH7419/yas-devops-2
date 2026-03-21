package com.yas.product.model;

import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.model.enumeration.DimensionUnit;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

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

    private Product makeProduct(Long id, String name, String slug) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setSlug(slug);
        return product;
    }

    private Product makeFullProduct(Long id) {
        return Product.builder()
                .id(id)
                .name("Test Product")
                .shortDescription("Short desc")
                .description("Full description")
                .specification("Specs")
                .sku("SKU-001")
                .gtin("1234567890")
                .slug("test-product")
                .price(99.99)
                .hasOptions(false)
                .isAllowedToOrder(true)
                .isPublished(true)
                .isFeatured(false)
                .isVisibleIndividually(true)
                .stockTrackingEnabled(true)
                .stockQuantity(100L)
                .taxClassId(1L)
                .metaTitle("Meta Title")
                .metaKeyword("meta,keyword")
                .metaDescription("Meta description")
                .thumbnailMediaId(10L)
                .weight(1.5)
                .dimensionUnit(DimensionUnit.CM)
                .length(10.0)
                .width(5.0)
                .height(3.0)
                .taxIncluded(true)
                .build();
    }

    // --- equals ---

    @Test
    void equals_WhenSameId_ShouldBeEqual() {
        Product p1 = makeProduct(7L, "Product A", "product-a");
        Product p2 = makeProduct(7L, "Product B", "product-b"); // different name/slug

        assertEquals(p1, p2);
    }

    @Test
    void equals_WhenDifferentId_ShouldNotBeEqual() {
        Product p1 = makeProduct(1L, "Product", "product");
        Product p2 = makeProduct(2L, "Product", "product");

        assertNotEquals(p1, p2);
    }

    @Test
    void equals_WhenBothIdAreNull_ShouldNotBeEqual() {
        Product p1 = makeProduct(null, "Product A", "product-a");
        Product p2 = makeProduct(null, "Product B", "product-b");

        assertNotEquals(p1, p2);
    }

    @Test
    void equals_WhenOneIdIsNull_ShouldNotBeEqual() {
        Product p1 = makeProduct(1L, "Product", "product");
        Product p2 = makeProduct(null, "Product", "product");

        assertNotEquals(p1, p2);
    }

    @Test
    void equals_WhenComparedToNull_ShouldReturnFalse() {
        Product product = makeProduct(1L, "Product", "product");

        assertNotEquals(null, product);
        assertNotEquals(product, null);
    }

    @Test
    void equals_WhenComparedToDifferentClass_ShouldReturnFalse() {
        Product product = makeProduct(1L, "Product", "product");

        assertNotEquals("Product", product);
        assertNotEquals(product, "Product");
    }

    @Test
    void equals_WhenComparedToSelf_ShouldReturnTrue() {
        Product product = makeProduct(1L, "Product", "product");

        assertEquals(product, product);
    }

    // --- hashCode ---

    @Test
    void hashCode_WhenSameId_ShouldBeEqual() {
        Product p1 = makeProduct(7L, "Product A", "product-a");
        Product p2 = makeProduct(7L, "Product B", "product-b");

        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void hashCode_ShouldBeDeterministic() {
        Product product = makeProduct(1L, "Product", "product");

        assertEquals(product.hashCode(), product.hashCode());
    }

    @Test
    void hashCode_ShouldNotThrowForNullId() {
        Product product = makeProduct(null, "Product", "product");

        assertDoesNotThrow(product::hashCode);
    }

    // --- primitive boolean fields via reflection ---

    @Test
    void isHasOptions_WhenTrue_ShouldReturnTrue() {
        Product product = makeProduct(1L, "Product", "product");
        setField(product, "hasOptions", true);

        assertTrue(product.isHasOptions());
    }

    @Test
    void isHasOptions_WhenFalse_ShouldReturnFalse() {
        Product product = makeProduct(1L, "Product", "product");
        setField(product, "hasOptions", false);

        assertFalse(product.isHasOptions());
    }

    @Test
    void isAllowedToOrder_WhenTrue_ShouldReturnTrue() {
        Product product = makeProduct(1L, "Product", "product");
        setField(product, "isAllowedToOrder", true);

        assertTrue(product.isAllowedToOrder());
    }

    @Test
    void isAllowedToOrder_WhenFalse_ShouldReturnFalse() {
        Product product = makeProduct(1L, "Product", "product");
        setField(product, "isAllowedToOrder", false);

        assertFalse(product.isAllowedToOrder());
    }

    @Test
    void isPublished_WhenTrue_ShouldReturnTrue() {
        Product product = makeProduct(1L, "Product", "product");
        setField(product, "isPublished", true);

        assertTrue(product.isPublished());
    }

    @Test
    void isPublished_WhenFalse_ShouldReturnFalse() {
        Product product = makeProduct(1L, "Product", "product");
        setField(product, "isPublished", false);

        assertFalse(product.isPublished());
    }

    @Test
    void isFeatured_WhenTrue_ShouldReturnTrue() {
        Product product = makeProduct(1L, "Product", "product");
        setField(product, "isFeatured", true);

        assertTrue(product.isFeatured());
    }

    @Test
    void isFeatured_WhenFalse_ShouldReturnFalse() {
        Product product = makeProduct(1L, "Product", "product");
        setField(product, "isFeatured", false);

        assertFalse(product.isFeatured());
    }

    @Test
    void isVisibleIndividually_WhenTrue_ShouldReturnTrue() {
        Product product = makeProduct(1L, "Product", "product");
        setField(product, "isVisibleIndividually", true);

        assertTrue(product.isVisibleIndividually());
    }

    @Test
    void isVisibleIndividually_WhenFalse_ShouldReturnFalse() {
        Product product = makeProduct(1L, "Product", "product");
        setField(product, "isVisibleIndividually", false);

        assertFalse(product.isVisibleIndividually());
    }

    @Test
    void isStockTrackingEnabled_WhenTrue_ShouldReturnTrue() {
        Product product = makeProduct(1L, "Product", "product");
        setField(product, "stockTrackingEnabled", true);

        assertTrue(product.isStockTrackingEnabled());
    }

    @Test
    void isStockTrackingEnabled_WhenFalse_ShouldReturnFalse() {
        Product product = makeProduct(1L, "Product", "product");
        setField(product, "stockTrackingEnabled", false);

        assertFalse(product.isStockTrackingEnabled());
    }

    @Test
    void isTaxIncluded_WhenTrue_ShouldReturnTrue() {
        Product product = makeProduct(1L, "Product", "product");
        setField(product, "taxIncluded", true);

        assertTrue(product.isTaxIncluded());
    }

    @Test
    void isTaxIncluded_WhenFalse_ShouldReturnFalse() {
        Product product = makeProduct(1L, "Product", "product");
        setField(product, "taxIncluded", false);

        assertFalse(product.isTaxIncluded());
    }

    // --- field accessors ---

    @Test
    void getters_ShouldReturnCorrectValues() {
        Product product = makeFullProduct(1L);

        assertEquals(1L, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("Short desc", product.getShortDescription());
        assertEquals("Full description", product.getDescription());
        assertEquals("Specs", product.getSpecification());
        assertEquals("SKU-001", product.getSku());
        assertEquals("1234567890", product.getGtin());
        assertEquals("test-product", product.getSlug());
        assertEquals(99.99, product.getPrice());
        assertEquals(100L, product.getStockQuantity());
        assertEquals(1L, product.getTaxClassId());
        assertEquals("Meta Title", product.getMetaTitle());
        assertEquals("meta,keyword", product.getMetaKeyword());
        assertEquals("Meta description", product.getMetaDescription());
        assertEquals(10L, product.getThumbnailMediaId());
        assertEquals(1.5, product.getWeight());
        assertEquals(DimensionUnit.CM, product.getDimensionUnit());
        assertEquals(10.0, product.getLength());
        assertEquals(5.0, product.getWidth());
        assertEquals(3.0, product.getHeight());
    }

    @Test
    void setters_ShouldUpdateFields() {
        Product product = new Product();

        product.setId(5L);
        product.setName("Updated Name");
        product.setShortDescription("Short");
        product.setDescription("Desc");
        product.setSpecification("Spec");
        product.setSku("NEW-SKU");
        product.setGtin("9876543210");
        product.setSlug("updated-slug");
        product.setPrice(49.99);
        product.setStockQuantity(50L);
        product.setTaxClassId(2L);
        product.setMetaTitle("New Meta");
        product.setMetaKeyword("new,meta");
        product.setMetaDescription("New meta desc");
        product.setThumbnailMediaId(20L);
        product.setWeight(2.5);
        product.setDimensionUnit(DimensionUnit.INCH);
        product.setLength(15.0);
        product.setWidth(8.0);
        product.setHeight(4.0);

        assertEquals(5L, product.getId());
        assertEquals("Updated Name", product.getName());
        assertEquals("Short", product.getShortDescription());
        assertEquals("Desc", product.getDescription());
        assertEquals("Spec", product.getSpecification());
        assertEquals("NEW-SKU", product.getSku());
        assertEquals("9876543210", product.getGtin());
        assertEquals("updated-slug", product.getSlug());
        assertEquals(49.99, product.getPrice());
        assertEquals(50L, product.getStockQuantity());
        assertEquals(2L, product.getTaxClassId());
        assertEquals("New Meta", product.getMetaTitle());
        assertEquals("new,meta", product.getMetaKeyword());
        assertEquals("New meta desc", product.getMetaDescription());
        assertEquals(20L, product.getThumbnailMediaId());
        assertEquals(2.5, product.getWeight());
        assertEquals(DimensionUnit.INCH, product.getDimensionUnit());
        assertEquals(15.0, product.getLength());
        assertEquals(8.0, product.getWidth());
        assertEquals(4.0, product.getHeight());
    }

    @Test
    void price_WhenZero_ShouldBeStored() {
        Product product = makeProduct(1L, "Product", "product");
        product.setPrice(0.0);

        assertEquals(0.0, product.getPrice());
    }

    @Test
    void stockQuantity_WhenZero_ShouldBeStored() {
        Product product = makeProduct(1L, "Product", "product");
        product.setStockQuantity(0L);

        assertEquals(0L, product.getStockQuantity());
    }

    @Test
    void dimensionUnit_WhenNull_ShouldBeNull() {
        Product product = makeProduct(1L, "Product", "product");

        assertNull(product.getDimensionUnit());
    }

    // --- relationships ---

    @Test
    void brand_WhenSet_ShouldBeAccessible() {
        Product product = makeProduct(1L, "Product", "product");
        Brand brand = new Brand();
        brand.setId(3L);
        brand.setName("Apple");
        brand.setSlug("apple");

        product.setBrand(brand);

        assertEquals(brand, product.getBrand());
        assertEquals("Apple", product.getBrand().getName());
    }

    @Test
    void brand_WhenNull_ShouldReturnNull() {
        Product product = makeProduct(1L, "Product", "product");

        assertNull(product.getBrand());
    }

    @Test
    void parent_WhenSet_ShouldBeAccessible() {
        Product parent = makeProduct(1L, "Parent", "parent-product");
        Product child = makeProduct(2L, "Child", "child-product");

        child.setParent(parent);

        assertEquals(parent, child.getParent());
    }

    @Test
    void parent_WhenNull_ShouldReturnNull() {
        Product product = makeProduct(1L, "Product", "product");

        assertNull(product.getParent());
    }

    @Test
    void products_WhenSet_ShouldBeAccessible() {
        Product parent = makeProduct(1L, "Parent", "parent");
        Product child1 = makeProduct(2L, "Child 1", "child-1");
        Product child2 = makeProduct(3L, "Child 2", "child-2");

        parent.setProducts(List.of(child1, child2));

        assertEquals(2, parent.getProducts().size());
        assertEquals("Child 1", parent.getProducts().get(0).getName());
        assertEquals("Child 2", parent.getProducts().get(1).getName());
    }

    @Test
    void products_WhenEmpty_ShouldReturnEmptyList() {
        Product product = makeProduct(1L, "Product", "product");

        assertNotNull(product.getProducts());
        assertTrue(product.getProducts().isEmpty());
    }

    @Test
    void products_ShouldNotBeNullByDefault() {
        Product product = new Product();

        assertNotNull(product.getProducts());
    }

    @Test
    void relatedProducts_WhenSet_ShouldBeAccessible() {
        Product product = makeProduct(1L, "Product", "product");
        ProductRelated related = new ProductRelated();
        related.setId(5L);
        related.setProduct(product);

        product.setRelatedProducts(List.of(related));

        assertEquals(1, product.getRelatedProducts().size());
        assertEquals(product, product.getRelatedProducts().get(0).getProduct());
    }

    @Test
    void relatedProducts_WhenEmpty_ShouldReturnEmptyList() {
        Product product = makeProduct(1L, "Product", "product");

        assertNotNull(product.getRelatedProducts());
        assertTrue(product.getRelatedProducts().isEmpty());
    }

    @Test
    void productCategories_WhenSet_ShouldBeAccessible() {
        Product product = makeProduct(1L, "Product", "product");
        Category category = new Category();
        category.setId(1L);
        ProductCategory pc = new ProductCategory();
        pc.setId(10L);
        pc.setProduct(product);
        pc.setCategory(category);

        product.setProductCategories(List.of(pc));

        assertEquals(1, product.getProductCategories().size());
        assertEquals(category, product.getProductCategories().get(0).getCategory());
    }

    @Test
    void productCategories_WhenEmpty_ShouldReturnEmptyList() {
        Product product = makeProduct(1L, "Product", "product");

        assertNotNull(product.getProductCategories());
        assertTrue(product.getProductCategories().isEmpty());
    }

    @Test
    void attributeValues_WhenSet_ShouldBeAccessible() {
        Product product = makeProduct(1L, "Product", "product");
        ProductAttributeValue pav = new ProductAttributeValue();
        pav.setId(20L);
        pav.setProduct(product);

        product.setAttributeValues(List.of(pav));

        assertEquals(1, product.getAttributeValues().size());
        assertEquals(product, product.getAttributeValues().get(0).getProduct());
    }

    @Test
    void attributeValues_WhenEmpty_ShouldReturnEmptyList() {
        Product product = makeProduct(1L, "Product", "product");

        assertNotNull(product.getAttributeValues());
        assertTrue(product.getAttributeValues().isEmpty());
    }

    @Test
    void productImages_WhenSet_ShouldBeAccessible() {
        Product product = makeProduct(1L, "Product", "product");
        ProductImage image = new ProductImage();
        image.setId(30L);
        image.setProduct(product);

        product.setProductImages(List.of(image));

        assertEquals(1, product.getProductImages().size());
        assertEquals(product, product.getProductImages().get(0).getProduct());
    }

    @Test
    void productImages_WhenEmpty_ShouldReturnEmptyList() {
        Product product = makeProduct(1L, "Product", "product");

        assertNotNull(product.getProductImages());
        assertTrue(product.getProductImages().isEmpty());
    }

    @Test
    void addProductImage_ShouldAddToList() {
        Product product = makeProduct(1L, "Product", "product");
        ProductImage image = new ProductImage();
        image.setId(40L);
        image.setProduct(product);

        product.getProductImages().add(image);

        assertEquals(1, product.getProductImages().size());
    }

    @Test
    void addRelatedProduct_ShouldAddToList() {
        Product product = makeProduct(1L, "Product", "product");
        ProductRelated related = new ProductRelated();
        related.setId(50L);
        related.setProduct(product);

        product.getRelatedProducts().add(related);

        assertEquals(1, product.getRelatedProducts().size());
    }

    @Test
    void addAttributeValue_ShouldAddToList() {
        Product product = makeProduct(1L, "Product", "product");
        ProductAttributeValue pav = new ProductAttributeValue();
        pav.setId(60L);
        pav.setProduct(product);

        product.getAttributeValues().add(pav);

        assertEquals(1, product.getAttributeValues().size());
    }

    // --- Builder ---

    @Test
    void builder_ShouldCreateProductWithAllFields() {
        Product product = makeFullProduct(99L);

        assertEquals(99L, product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals("Short desc", product.getShortDescription());
        assertEquals("Full description", product.getDescription());
        assertEquals("Specs", product.getSpecification());
        assertEquals("SKU-001", product.getSku());
        assertEquals("1234567890", product.getGtin());
        assertEquals("test-product", product.getSlug());
        assertEquals(99.99, product.getPrice());
        assertFalse(product.isHasOptions());
        assertTrue(product.isAllowedToOrder());
        assertTrue(product.isPublished());
        assertFalse(product.isFeatured());
        assertTrue(product.isVisibleIndividually());
        assertTrue(product.isStockTrackingEnabled());
        assertEquals(100L, product.getStockQuantity());
        assertEquals(1L, product.getTaxClassId());
        assertEquals("Meta Title", product.getMetaTitle());
        assertEquals("meta,keyword", product.getMetaKeyword());
        assertEquals("Meta description", product.getMetaDescription());
        assertEquals(10L, product.getThumbnailMediaId());
        assertEquals(1.5, product.getWeight());
        assertEquals(DimensionUnit.CM, product.getDimensionUnit());
        assertEquals(10.0, product.getLength());
        assertEquals(5.0, product.getWidth());
        assertEquals(3.0, product.getHeight());
        assertTrue(product.isTaxIncluded());
    }

    @Test
    void builder_DefaultLists_ShouldNotBeNull() {
        Product product = Product.builder().name("Test").slug("test").build();

        assertNotNull(product.getProducts());
        assertNotNull(product.getRelatedProducts());
        assertNotNull(product.getProductCategories());
        assertNotNull(product.getAttributeValues());
        assertNotNull(product.getProductImages());
    }

    @Test
    void builder_ShouldAllowPartialFields() {
        Product product = Product.builder()
                .name("Minimal Product")
                .slug("minimal-product")
                .build();

        assertNull(product.getId());
        assertEquals("Minimal Product", product.getName());
        assertEquals("minimal-product", product.getSlug());
        assertNull(product.getPrice());
        assertNull(product.getSku());
    }
}
