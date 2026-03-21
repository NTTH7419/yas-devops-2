package com.yas.promotion.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yas.promotion.model.Promotion;
import com.yas.promotion.model.PromotionApply;
import com.yas.promotion.model.enumeration.ApplyTo;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;

class PromotionPutVmTest {

    @Test
    void testBuilder() {
        Date startDate = Date.from(Instant.now());
        Date endDate = Date.from(Instant.now().plusSeconds(86400));

        PromotionPutVm putVm = PromotionPutVm.builder()
            .id(1L)
            .name("Test Promotion")
            .slug("test-promotion")
            .description("Description")
            .couponCode("TESTCODE")
            .minimumOrderPurchaseAmount(100L)
            .isActive(true)
            .startDate(startDate)
            .endDate(endDate)
            .build();

        assertNotNull(putVm);
        assertEquals(1L, putVm.getId());
        assertEquals("Test Promotion", putVm.getName());
        assertEquals("test-promotion", putVm.getSlug());
        assertEquals("Description", putVm.getDescription());
        assertEquals("TESTCODE", putVm.getCouponCode());
        assertEquals(100L, putVm.getMinimumOrderPurchaseAmount());
        assertTrue(putVm.getIsActive());
    }

    @Test
    void testCreatePromotionApplies_ForProduct() {
        Promotion promotion = Promotion.builder()
            .id(1L)
            .applyTo(ApplyTo.PRODUCT)
            .build();

        PromotionPutVm putVm = new PromotionPutVm();
        putVm.setProductIds(List.of(1L, 2L, 3L));

        List<PromotionApply> applies = PromotionPutVm.createPromotionApplies(putVm, promotion);

        assertEquals(3, applies.size());
        assertEquals(1L, applies.get(0).getProductId());
        assertEquals(2L, applies.get(1).getProductId());
        assertEquals(3L, applies.get(2).getProductId());
    }

    @Test
    void testCreatePromotionApplies_ForBrand() {
        Promotion promotion = Promotion.builder()
            .id(1L)
            .applyTo(ApplyTo.BRAND)
            .build();

        PromotionPutVm putVm = new PromotionPutVm();
        putVm.setBrandIds(List.of(10L, 20L));

        List<PromotionApply> applies = PromotionPutVm.createPromotionApplies(putVm, promotion);

        assertEquals(2, applies.size());
        assertEquals(10L, applies.get(0).getBrandId());
        assertEquals(20L, applies.get(1).getBrandId());
    }

    @Test
    void testCreatePromotionApplies_ForCategory() {
        Promotion promotion = Promotion.builder()
            .id(1L)
            .applyTo(ApplyTo.CATEGORY)
            .build();

        PromotionPutVm putVm = new PromotionPutVm();
        putVm.setCategoryIds(List.of(100L, 200L, 300L));

        List<PromotionApply> applies = PromotionPutVm.createPromotionApplies(putVm, promotion);

        assertEquals(3, applies.size());
        assertEquals(100L, applies.get(0).getCategoryId());
        assertEquals(200L, applies.get(1).getCategoryId());
        assertEquals(300L, applies.get(2).getCategoryId());
    }

    @Test
    void testSetters() {
        PromotionPutVm putVm = new PromotionPutVm();
        putVm.setId(1L);
        putVm.setName("Updated Name");
        putVm.setSlug("updated-slug");
        putVm.setDescription("Updated Description");
        putVm.setCouponCode("UPDATED");
        putVm.setMinimumOrderPurchaseAmount(200L);
        putVm.setIsActive(false);
        putVm.setStartDate(new Date());
        putVm.setEndDate(new Date());

        assertEquals(1L, putVm.getId());
        assertEquals("Updated Name", putVm.getName());
        assertEquals("updated-slug", putVm.getSlug());
        assertEquals("Updated Description", putVm.getDescription());
        assertEquals("UPDATED", putVm.getCouponCode());
        assertEquals(200L, putVm.getMinimumOrderPurchaseAmount());
        assertEquals(false, putVm.getIsActive());
    }
}
