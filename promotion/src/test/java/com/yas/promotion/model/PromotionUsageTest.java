package com.yas.promotion.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class PromotionUsageTest {

    @Test
    void testPromotionUsageBuilder() {
        Promotion promotion = Promotion.builder()
            .id(1L)
            .name("Test Promotion")
            .build();

        PromotionUsage promotionUsage = PromotionUsage.builder()
            .id(1L)
            .promotion(promotion)
            .productId(100L)
            .userId("user123")
            .orderId(500L)
            .build();

        assertNotNull(promotionUsage);
        assertEquals(1L, promotionUsage.getId());
        assertEquals(promotion, promotionUsage.getPromotion());
        assertEquals(100L, promotionUsage.getProductId());
        assertEquals("user123", promotionUsage.getUserId());
        assertEquals(500L, promotionUsage.getOrderId());
    }

    @Test
    void testPromotionUsageSetters() {
        Promotion promotion = Promotion.builder()
            .id(1L)
            .name("Test Promotion")
            .build();

        PromotionUsage promotionUsage = new PromotionUsage();
        promotionUsage.setId(1L);
        promotionUsage.setPromotion(promotion);
        promotionUsage.setProductId(100L);
        promotionUsage.setUserId("user123");
        promotionUsage.setOrderId(500L);

        assertEquals(1L, promotionUsage.getId());
        assertEquals(promotion, promotionUsage.getPromotion());
        assertEquals(100L, promotionUsage.getProductId());
        assertEquals("user123", promotionUsage.getUserId());
        assertEquals(500L, promotionUsage.getOrderId());
    }

    @Test
    void testPromotionUsageBuilderWithNoId() {
        Promotion promotion = Promotion.builder()
            .id(1L)
            .name("Test Promotion")
            .build();

        PromotionUsage promotionUsage = PromotionUsage.builder()
            .promotion(promotion)
            .productId(100L)
            .userId("user123")
            .orderId(500L)
            .build();

        assertNotNull(promotionUsage);
        assertEquals(promotion, promotionUsage.getPromotion());
        assertEquals(100L, promotionUsage.getProductId());
        assertEquals("user123", promotionUsage.getUserId());
        assertEquals(500L, promotionUsage.getOrderId());
    }
}
