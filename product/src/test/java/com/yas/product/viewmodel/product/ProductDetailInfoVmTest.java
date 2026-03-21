package com.yas.product.viewmodel.product;

import com.yas.product.viewmodel.ImageVm;
import com.yas.product.viewmodel.productattribute.ProductAttributeValueGetVm;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProductDetailInfoVmTest {

    private static ImageVm img(Long id, String url) {
        return new ImageVm(id, url);
    }

    // Full constructor: 25 args
    // 1-5:   id, name, shortDescription, description, specification
    // 6-8:   sku, gtin, slug
    // 9-13:  isAllowedToOrder, isPublished, isFeatured, isVisible, stockTrackingEnabled
    // 14-15: price, brandId
    // 16:    categories
    // 17-19: metaTitle, metaKeyword, metaDescription
    // 20-21: taxClassId, brandName
    // 22:    attributeValues
    // 23-25: variations, thumbnail, productImages
    private ProductDetailInfoVm makeVmFull(
            long id, String name,
            List<ProductAttributeValueGetVm> attrs,
            List<ProductVariationGetVm> variations,
            List<ImageVm> images,
            ImageVm thumbnail) {
        return new ProductDetailInfoVm(
                id, name, "short-desc", "full-desc", "specs",
                "SKU-001", "GTIN001", "test-product",
                Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, 999.99, 5L, null,
                "SEO Title", "keywords", "meta desc",
                1L, "BrandX",
                attrs, variations, thumbnail, images
        );
    }

    // --- constructor full args ---

    @Test
    void constructor_WithAllFieldsProvided_ShouldSetAllFieldsCorrectly() {
        List<ProductAttributeValueGetVm> attrs = List.of(
                new ProductAttributeValueGetVm(10L, "Color", "Red"),
                new ProductAttributeValueGetVm(11L, "Size", "M")
        );
        List<ProductVariationGetVm> variations = List.of(
                new ProductVariationGetVm(20L, "Var1", "var1", "SKU-V1", "GT-V1",
                        999.0, img(1L, "/img/thumb.jpg"), List.of(), Map.of(1L, "Opt1"))
        );
        ImageVm thumbnail = img(2L, "/img/thumb.png");
        List<ImageVm> images = List.of(img(3L, "/img/img1.jpg"));

        ProductDetailInfoVm vm = makeVmFull(1L, "iPhone 14 Pro", attrs, variations, images, thumbnail);

        assertEquals(1L, vm.getId());
        assertEquals("iPhone 14 Pro", vm.getName());
        assertEquals("short-desc", vm.getShortDescription());
        assertEquals("full-desc", vm.getDescription());
        assertEquals("specs", vm.getSpecification());
        assertEquals("SKU-001", vm.getSku());
        assertEquals("GTIN001", vm.getGtin());
        assertEquals("test-product", vm.getSlug());
        assertTrue(vm.getIsAllowedToOrder());
        assertTrue(vm.getIsPublished());
        assertFalse(vm.getIsFeatured());
        assertTrue(vm.getIsVisible());
        assertTrue(vm.getStockTrackingEnabled());
        assertEquals(999.99, vm.getPrice());
        assertEquals(5L, vm.getBrandId());
        assertEquals("SEO Title", vm.getMetaTitle());
        assertEquals("keywords", vm.getMetaKeyword());
        assertEquals("meta desc", vm.getMetaDescription());
        assertEquals(1L, vm.getTaxClassId());
        assertEquals("BrandX", vm.getBrandName());
        assertEquals(2, vm.getAttributeValues().size());
        assertEquals("Color", vm.getAttributeValues().get(0).nameProductAttribute());
        assertEquals(1, vm.getVariations().size());
        assertEquals("Opt1", vm.getVariations().get(0).options().get(1L));
        assertEquals(thumbnail, vm.getThumbnail());
        assertEquals(1, vm.getProductImages().size());
    }

    // --- null list defaults ---

    @Test
    void constructor_WhenCategoriesIsNull_ShouldDefaultToEmptyList() {
        ProductDetailInfoVm vm = makeVmFull(1L, "P", null, null, null, null);
        assertNotNull(vm.getCategories());
        assertTrue(vm.getCategories().isEmpty());
    }

    @Test
    void constructor_WhenAttributeValuesIsNull_ShouldDefaultToEmptyList() {
        ProductDetailInfoVm vm = makeVmFull(1L, "P", null, null, null, null);
        assertNotNull(vm.getAttributeValues());
        assertTrue(vm.getAttributeValues().isEmpty());
    }

    @Test
    void constructor_WhenVariationsIsNull_ShouldDefaultToEmptyList() {
        ProductDetailInfoVm vm = makeVmFull(1L, "P", null, null, null, null);
        assertNotNull(vm.getVariations());
        assertTrue(vm.getVariations().isEmpty());
    }

    @Test
    void constructor_WhenProductImagesIsNull_ShouldRemainNull() {
        ProductDetailInfoVm vm = makeVmFull(1L, "P", null, null, null, null);
        assertNull(vm.getProductImages());
    }

    // --- multiple items in lists ---

    @Test
    void constructor_WhenMultipleListsProvided_ShouldSetAllListsCorrectly() {
        List<ProductAttributeValueGetVm> attrs = List.of(
                new ProductAttributeValueGetVm(1L, "Size", "L"),
                new ProductAttributeValueGetVm(2L, "Weight", "200g"),
                new ProductAttributeValueGetVm(3L, "Material", "Cotton")
        );
        List<ProductVariationGetVm> variations = List.of(
                new ProductVariationGetVm(100L, "Var1", "v1", "S1", "G1", 50.0, null, List.of(), Map.of()),
                new ProductVariationGetVm(101L, "Var2", "v2", "S2", "G2", 60.0, null, List.of(), Map.of())
        );
        List<ImageVm> images = List.of(img(1L, "/img/a.jpg"), img(2L, "/img/b.jpg"), img(3L, "/img/c.jpg"));
        ImageVm thumbnail = img(0L, "/img/thumb.jpg");

        ProductDetailInfoVm vm = makeVmFull(99L, "Multi Product", attrs, variations, images, thumbnail);

        assertEquals(3, vm.getAttributeValues().size());
        assertEquals(2, vm.getVariations().size());
        assertEquals(3, vm.getProductImages().size());
        assertEquals(thumbnail, vm.getThumbnail());
        assertEquals("Var1", vm.getVariations().get(0).name());
        assertEquals("Size", vm.getAttributeValues().get(0).nameProductAttribute());
    }

    // --- boolean flags ---

    @Test
    void constructor_WhenAllBooleanFlagsAreFalse_ShouldSetAllCorrectly() {
        ProductDetailInfoVm vm = new ProductDetailInfoVm(
                1L, "Hidden Product", null, null, null, "SKU-X", "GTIN-X", "hidden-product",
                Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Boolean.FALSE, Double.valueOf(0.0),
                null, null, null, null, null, null,
                null, null, null, null, null
        );

        assertFalse(vm.getIsAllowedToOrder());
        assertFalse(vm.getIsPublished());
        assertFalse(vm.getIsFeatured());
        assertFalse(vm.getIsVisible());
        assertFalse(vm.getStockTrackingEnabled());
        assertEquals("Hidden Product", vm.getName());
        assertEquals("SKU-X", vm.getSku());
        assertEquals(Double.valueOf(0.0), vm.getPrice());
    }

    @Test
    void constructor_WhenPriceIsZero_ShouldBeAccepted() {
        ProductDetailInfoVm vm = new ProductDetailInfoVm(
                1L, "Free Product", null, null, null, "SKU-FREE", "GTIN-FREE", "free-product",
                Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, Double.valueOf(0.0),
                null, null, null, null, null, null,
                null, null, null, null, null
        );
        assertEquals(Double.valueOf(0.0), vm.getPrice());
        assertEquals("Free Product", vm.getName());
    }

    @Test
    void constructor_WhenVariationOptionsAreProvided_ShouldStoreMapCorrectly() {
        Map<Long, String> options = Map.of(1L, "Red", 2L, "Large", 3L, "Cotton");
        List<ProductVariationGetVm> variations = List.of(
                new ProductVariationGetVm(1L, "Full Option", "full-opt", "SKU-FULL", "GTIN-FULL",
                        150.0, img(1L, "/t.jpg"), List.of(), options)
        );

        ProductDetailInfoVm vm = makeVmFull(1L, "P", null, variations, null, null);

        assertEquals(3, vm.getVariations().get(0).options().size());
        assertEquals("Red", vm.getVariations().get(0).options().get(1L));
        assertEquals("Large", vm.getVariations().get(0).options().get(2L));
    }
}
