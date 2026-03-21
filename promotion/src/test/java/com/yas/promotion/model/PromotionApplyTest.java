package com.yas.promotion.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class PromotionApplyTest {

    @Test
    void testPromotionApplyBuilder() {
        Promotion promotion = Promotion.builder()
            .id(1L)
            .name("Test Promotion")
            .build();

        PromotionApply promotionApply = PromotionApply.builder()
            .id(1L)
            .promotion(promotion)
            .productId(100L)
            .categoryId(null)
            .brandId(null)
            .build();

        assertNotNull(promotionApply);
        assertEquals(1L, promotionApply.getId());
        assertEquals(promotion, promotionApply.getPromotion());
        assertEquals(100L, promotionApply.getProductId());
        assertNull(promotionApply.getCategoryId());
        assertNull(promotionApply.getBrandId());
    }

    @Test
    void testPromotionApplyBuilder_WithCategory() {
        Promotion promotion = Promotion.builder()
            .id(1L)
            .name("Test Promotion")
            .build();

        PromotionApply promotionApply = PromotionApply.builder()
            .id(1L)
            .promotion(promotion)
            .productId(null)
            .categoryId(200L)
            .brandId(null)
            .build();

        assertEquals(200L, promotionApply.getCategoryId());
        assertNull(promotionApply.getProductId());
    }

    @Test
    void testPromotionApplyBuilder_WithBrand() {
        Promotion promotion = Promotion.builder()
            .id(1L)
            .name("Test Promotion")
            .build();

        PromotionApply promotionApply = PromotionApply.builder()
            .id(1L)
            .promotion(promotion)
            .productId(null)
            .categoryId(null)
            .brandId(300L)
            .build();

        assertEquals(300L, promotionApply.getBrandId());
        assertNull(promotionApply.getProductId());
        assertNull(promotionApply.getCategoryId());
    }

    @Test
    void testPromotionApplySetters() {
        Promotion promotion = Promotion.builder()
            .id(1L)
            .name("Test Promotion")
            .build();

        PromotionApply promotionApply = new PromotionApply();
        promotionApply.setId(1L);
        promotionApply.setPromotion(promotion);
        promotionApply.setProductId(100L);
        promotionApply.setCategoryId(200L);
        promotionApply.setBrandId(300L);

        assertEquals(1L, promotionApply.getId());
        assertEquals(promotion, promotionApply.getPromotion());
        assertEquals(100L, promotionApply.getProductId());
        assertEquals(200L, promotionApply.getCategoryId());
        assertEquals(300L, promotionApply.getBrandId());
    }
}
