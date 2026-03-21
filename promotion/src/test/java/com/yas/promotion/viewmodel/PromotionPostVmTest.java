package com.yas.promotion.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.promotion.model.Promotion;
import com.yas.promotion.model.PromotionApply;
import com.yas.promotion.model.enumeration.ApplyTo;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;

class PromotionPostVmTest {

    @Test
    void testBuilder() {
        Date startDate = Date.from(Instant.now());
        Date endDate = Date.from(Instant.now().plusSeconds(86400));

        PromotionPostVm postVm = PromotionPostVm.builder()
            .name("Test Promotion")
            .slug("test-promotion")
            .description("Description")
            .couponCode("TESTCODE")
            .minimumOrderPurchaseAmount(100L)
            .isActive(true)
            .startDate(startDate)
            .endDate(endDate)
            .build();

        assertNotNull(postVm);
        assertEquals("Test Promotion", postVm.getName());
        assertEquals("test-promotion", postVm.getSlug());
        assertEquals("Description", postVm.getDescription());
        assertEquals("TESTCODE", postVm.getCouponCode());
        assertEquals(100L, postVm.getMinimumOrderPurchaseAmount());
    }

    @Test
    void testCreatePromotionApplies_ForProduct() {
        Promotion promotion = Promotion.builder()
            .id(1L)
            .applyTo(ApplyTo.PRODUCT)
            .build();

        PromotionPostVm postVm = new PromotionPostVm();
        postVm.setProductIds(List.of(1L, 2L, 3L));

        List<PromotionApply> applies = PromotionPostVm.createPromotionApplies(postVm, promotion);

        assertEquals(3, applies.size());
        assertEquals(1L, applies.get(0).getProductId());
    }

    @Test
    void testCreatePromotionApplies_ForBrand() {
        Promotion promotion = Promotion.builder()
            .id(1L)
            .applyTo(ApplyTo.BRAND)
            .build();

        PromotionPostVm postVm = new PromotionPostVm();
        postVm.setBrandIds(List.of(10L, 20L));

        List<PromotionApply> applies = PromotionPostVm.createPromotionApplies(postVm, promotion);

        assertEquals(2, applies.size());
        assertEquals(10L, applies.get(0).getBrandId());
    }

    @Test
    void testCreatePromotionApplies_ForCategory() {
        Promotion promotion = Promotion.builder()
            .id(1L)
            .applyTo(ApplyTo.CATEGORY)
            .build();

        PromotionPostVm postVm = new PromotionPostVm();
        postVm.setCategoryIds(List.of(100L, 200L));

        List<PromotionApply> applies = PromotionPostVm.createPromotionApplies(postVm, promotion);

        assertEquals(2, applies.size());
        assertEquals(100L, applies.get(0).getCategoryId());
    }

    @Test
    void testSetters() {
        PromotionPostVm postVm = new PromotionPostVm();
        postVm.setName("Updated Name");
        postVm.setSlug("updated-slug");
        postVm.setDescription("Updated Description");
        postVm.setCouponCode("UPDATED");
        postVm.setMinimumOrderPurchaseAmount(200L);
        postVm.setActive(false);
        postVm.setStartDate(new Date());
        postVm.setEndDate(new Date());

        assertEquals("Updated Name", postVm.getName());
        assertEquals("updated-slug", postVm.getSlug());
        assertEquals("Updated Description", postVm.getDescription());
        assertEquals("UPDATED", postVm.getCouponCode());
        assertEquals(200L, postVm.getMinimumOrderPurchaseAmount());
    }
}
