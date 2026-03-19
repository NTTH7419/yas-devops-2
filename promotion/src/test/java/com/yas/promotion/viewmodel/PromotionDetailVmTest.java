package com.yas.promotion.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.yas.promotion.model.Promotion;
import com.yas.promotion.model.enumeration.ApplyTo;
import com.yas.promotion.model.enumeration.DiscountType;
import com.yas.promotion.model.enumeration.UsageType;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class PromotionDetailVmTest {

    @Test
    void testFromModel() {
        Instant now = Instant.now();
        Promotion promotion = Promotion.builder()
            .id(1L)
            .name("Test Promotion")
            .slug("test-promotion")
            .description("Test Description")
            .couponCode("TESTCODE")
            .discountType(DiscountType.PERCENTAGE)
            .usageType(UsageType.LIMITED)
            .applyTo(ApplyTo.PRODUCT)
            .usageLimit(100)
            .usageCount(10)
            .discountPercentage(10L)
            .discountAmount(null)
            .minimumOrderPurchaseAmount(50L)
            .isActive(true)
            .startDate(now)
            .endDate(now.plusSeconds(86400))
            .build();

        PromotionDetailVm detailVm = PromotionDetailVm.fromModel(promotion);

        assertNotNull(detailVm);
        assertEquals(1L, detailVm.id());
        assertEquals("Test Promotion", detailVm.name());
        assertEquals("test-promotion", detailVm.slug());
        assertEquals("Test Description", detailVm.description());
        assertEquals("TESTCODE", detailVm.couponCode());
        assertEquals(DiscountType.PERCENTAGE, detailVm.discountType());
        assertEquals(UsageType.LIMITED, detailVm.usageType());
        assertEquals(ApplyTo.PRODUCT, detailVm.applyTo());
        assertEquals(100, detailVm.usageLimit());
        assertEquals(10, detailVm.usageCount());
        assertEquals(10L, detailVm.discountPercentage());
        assertNull(detailVm.discountAmount());
        assertEquals(50L, detailVm.minimumOrderPurchaseAmount());
        assertEquals(true, detailVm.isActive());
    }

    @Test
    void testFromModelWithBrandCategoryProduct() {
        Instant now = Instant.now();
        Promotion promotion = Promotion.builder()
            .id(1L)
            .name("Test Promotion")
            .slug("test-promotion")
            .couponCode("TESTCODE")
            .discountType(DiscountType.FIXED)
            .usageType(UsageType.UNLIMITED)
            .applyTo(ApplyTo.BRAND)
            .usageLimit(0)
            .usageCount(0)
            .discountAmount(25L)
            .isActive(true)
            .startDate(now)
            .endDate(now.plusSeconds(86400))
            .build();

        BrandVm brand = new BrandVm(1L, "Brand", "brand", true);
        CategoryGetVm category = new CategoryGetVm(1L, "Category", "category", 0L);
        ProductVm product = new ProductVm(1L, "Product", "product", true, true, false, true, 100.0, null, 1L);

        PromotionDetailVm detailVm = PromotionDetailVm.fromModel(promotion, List.of(brand), List.of(category), List.of(product));

        assertNotNull(detailVm);
        assertEquals(1, detailVm.brands().size());
        assertEquals(1, detailVm.categories().size());
        assertEquals(1, detailVm.products().size());
        assertEquals("Brand", detailVm.brands().getFirst().name());
        assertEquals("Category", detailVm.categories().getFirst().name());
        assertEquals("Product", detailVm.products().getFirst().name());
    }

    @Test
    void testBuilder() {
        PromotionDetailVm detailVm = PromotionDetailVm.builder()
            .id(1L)
            .name("Test Promotion")
            .slug("test-promotion")
            .description("Test Description")
            .couponCode("TESTCODE")
            .discountType(DiscountType.PERCENTAGE)
            .usageType(UsageType.LIMITED)
            .applyTo(ApplyTo.PRODUCT)
            .usageLimit(100)
            .usageCount(10)
            .discountPercentage(10L)
            .discountAmount(null)
            .minimumOrderPurchaseAmount(50L)
            .isActive(true)
            .build();

        assertNotNull(detailVm);
        assertEquals(1L, detailVm.id());
        assertEquals("Test Promotion", detailVm.name());
    }
}
