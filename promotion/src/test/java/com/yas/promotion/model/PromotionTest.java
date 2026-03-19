package com.yas.promotion.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yas.promotion.model.enumeration.ApplyTo;
import com.yas.promotion.model.enumeration.DiscountType;
import com.yas.promotion.model.enumeration.UsageType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PromotionTest {

    @Test
    void testPromotionBuilder() {
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
            .usageCount(0)
            .discountPercentage(10L)
            .discountAmount(null)
            .minimumOrderPurchaseAmount(50L)
            .isActive(true)
            .startDate(now)
            .endDate(now.plusSeconds(86400))
            .build();

        assertNotNull(promotion);
        assertEquals(1L, promotion.getId());
        assertEquals("Test Promotion", promotion.getName());
        assertEquals("test-promotion", promotion.getSlug());
        assertEquals("TESTCODE", promotion.getCouponCode());
        assertEquals(DiscountType.PERCENTAGE, promotion.getDiscountType());
        assertEquals(UsageType.LIMITED, promotion.getUsageType());
        assertEquals(ApplyTo.PRODUCT, promotion.getApplyTo());
        assertEquals(100, promotion.getUsageLimit());
        assertEquals(0, promotion.getUsageCount());
        assertEquals(10L, promotion.getDiscountPercentage());
        assertEquals(50L, promotion.getMinimumOrderPurchaseAmount());
        assertTrue(promotion.getIsActive());
    }

    @Test
    void testPromotionEquals_SameId_ReturnsTrue() {
        Promotion promotion1 = Promotion.builder()
            .id(1L)
            .name("Promotion 1")
            .slug("promotion-1")
            .build();

        Promotion promotion2 = Promotion.builder()
            .id(1L)
            .name("Promotion 2")
            .slug("promotion-2")
            .build();

        assertEquals(promotion1, promotion2);
    }

    @Test
    void testPromotionEquals_DifferentId_ReturnsFalse() {
        Promotion promotion1 = Promotion.builder()
            .id(1L)
            .name("Promotion 1")
            .slug("promotion-1")
            .build();

        Promotion promotion2 = Promotion.builder()
            .id(2L)
            .name("Promotion 1")
            .slug("promotion-1")
            .build();

        assertNotEquals(promotion1, promotion2);
    }

    @Test
    void testPromotionEquals_NullId_ReturnsFalse() {
        Promotion promotion1 = Promotion.builder()
            .name("Promotion 1")
            .slug("promotion-1")
            .build();

        Promotion promotion2 = Promotion.builder()
            .name("Promotion 1")
            .slug("promotion-1")
            .build();

        assertNotEquals(promotion1, promotion2);
    }

    @Test
    void testPromotionEquals_NullObject_ReturnsFalse() {
        Promotion promotion = Promotion.builder()
            .id(1L)
            .name("Promotion 1")
            .build();

        assertNotEquals(promotion, null);
    }

    @Test
    void testPromotionEquals_DifferentClass_ReturnsFalse() {
        Promotion promotion = Promotion.builder()
            .id(1L)
            .name("Promotion 1")
            .build();

        assertNotEquals(promotion, "not a promotion");
    }

    @Test
    void testPromotionHashCode() {
        Promotion promotion1 = Promotion.builder()
            .id(1L)
            .name("Promotion 1")
            .slug("promotion-1")
            .build();

        Promotion promotion2 = Promotion.builder()
            .id(1L)
            .name("Promotion 2")
            .slug("promotion-2")
            .build();

        // Same id should have same hashCode
        assertEquals(promotion1.hashCode(), promotion2.hashCode());
    }

    @Test
    void testSetPromotionApplies() {
        Promotion promotion = Promotion.builder()
            .id(1L)
            .name("Test Promotion")
            .build();

        List<PromotionApply> applies = new ArrayList<>();
        PromotionApply apply1 = PromotionApply.builder()
            .id(1L)
            .promotion(promotion)
            .productId(1L)
            .build();
        applies.add(apply1);

        promotion.setPromotionApplies(applies);

        assertEquals(1, promotion.getPromotionApplies().size());
        assertEquals(1L, promotion.getPromotionApplies().getFirst().getProductId());
    }

    @Test
    void testSetPromotionApplies_ReplaceExisting() {
        Promotion promotion = Promotion.builder()
            .id(1L)
            .name("Test Promotion")
            .build();

        // Add first set
        List<PromotionApply> applies1 = new ArrayList<>();
        PromotionApply apply1 = PromotionApply.builder()
            .id(1L)
            .promotion(promotion)
            .productId(1L)
            .build();
        applies1.add(apply1);
        promotion.setPromotionApplies(applies1);

        assertEquals(1, promotion.getPromotionApplies().size());

        // Replace with new set
        List<PromotionApply> applies2 = new ArrayList<>();
        PromotionApply apply2 = PromotionApply.builder()
            .id(2L)
            .promotion(promotion)
            .productId(2L)
            .build();
        applies2.add(apply2);
        promotion.setPromotionApplies(applies2);

        assertEquals(1, promotion.getPromotionApplies().size());
        assertEquals(2L, promotion.getPromotionApplies().getFirst().getProductId());
    }

    @Test
    void testPromotionSetters() {
        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("Updated Name");
        promotion.setSlug("updated-slug");
        promotion.setDescription("Updated Description");
        promotion.setCouponCode("UPDATED");
        promotion.setDiscountType(DiscountType.FIXED);
        promotion.setUsageType(UsageType.UNLIMITED);
        promotion.setApplyTo(ApplyTo.BRAND);
        promotion.setUsageLimit(50);
        promotion.setUsageCount(10);
        promotion.setDiscountPercentage(null);
        promotion.setDiscountAmount(25L);
        promotion.setMinimumOrderPurchaseAmount(100L);
        promotion.setIsActive(false);

        assertEquals(1L, promotion.getId());
        assertEquals("Updated Name", promotion.getName());
        assertEquals("updated-slug", promotion.getSlug());
        assertEquals("Updated Description", promotion.getDescription());
        assertEquals("UPDATED", promotion.getCouponCode());
        assertEquals(DiscountType.FIXED, promotion.getDiscountType());
        assertEquals(UsageType.UNLIMITED, promotion.getUsageType());
        assertEquals(ApplyTo.BRAND, promotion.getApplyTo());
        assertEquals(50, promotion.getUsageLimit());
        assertEquals(10, promotion.getUsageCount());
        assertEquals(null, promotion.getDiscountPercentage());
        assertEquals(25L, promotion.getDiscountAmount());
        assertEquals(100L, promotion.getMinimumOrderPurchaseAmount());
        assertFalse(promotion.getIsActive());
    }
}
