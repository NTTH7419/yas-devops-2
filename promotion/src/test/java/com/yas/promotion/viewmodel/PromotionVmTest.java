package com.yas.promotion.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.promotion.model.Promotion;
import com.yas.promotion.model.enumeration.DiscountType;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class PromotionVmTest {

    @Test
    void testFromModel() {
        Instant now = Instant.now();
        Promotion promotion = Promotion.builder()
            .id(1L)
            .name("Test Promotion")
            .slug("test-promotion")
            .couponCode("TESTCODE")
            .discountType(DiscountType.PERCENTAGE)
            .discountPercentage(10L)
            .discountAmount(null)
            .isActive(true)
            .startDate(now)
            .endDate(now.plusSeconds(86400))
            .build();

        PromotionVm vm = PromotionVm.fromModel(promotion);

        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals("Test Promotion", vm.name());
        assertEquals("test-promotion", vm.slug());
        assertEquals(10L, vm.discountPercentage());
        assertEquals(null, vm.discountAmount());
        assertEquals(true, vm.isActive());
    }

    @Test
    void testBuilder() {
        Instant now = Instant.now();

        PromotionVm vm = PromotionVm.builder()
            .id(1L)
            .name("Test Promotion")
            .slug("test-promotion")
            .couponCode("TESTCODE")
            .discountPercentage(20L)
            .discountAmount(100L)
            .isActive(false)
            .startDate(now)
            .endDate(now.plusSeconds(86400))
            .build();

        assertNotNull(vm);
        assertEquals(1L, vm.id());
        assertEquals("Test Promotion", vm.name());
        assertEquals(20L, vm.discountPercentage());
        assertEquals(100L, vm.discountAmount());
    }
}
