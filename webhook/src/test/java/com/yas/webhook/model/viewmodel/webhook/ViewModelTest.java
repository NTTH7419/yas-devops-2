package com.yas.webhook.model.viewmodel.webhook;

import static org.junit.jupiter.api.Assertions.*;

import com.yas.webhook.model.enums.EventName;
import java.util.List;
import org.junit.jupiter.api.Test;

class ViewModelTest {

    // --- WebhookPostVm ---

    @Test
    void webhookPostVm_setterAndGetterShouldWork() {
        WebhookPostVm vm = new WebhookPostVm();
        vm.setPayloadUrl("https://example.com");
        vm.setSecret("secret");
        vm.setContentType("application/json");
        vm.setIsActive(true);
        vm.setEvents(List.of(EventVm.builder().id(1L).name(EventName.ON_ORDER_CREATED).build()));

        assertEquals("https://example.com", vm.getPayloadUrl());
        assertEquals("secret", vm.getSecret());
        assertEquals("application/json", vm.getContentType());
        assertTrue(vm.getIsActive());
        assertEquals(1, vm.getEvents().size());
    }

    // --- WebhookDetailVm ---

    @Test
    void webhookDetailVm_setterAndGetterShouldWork() {
        WebhookDetailVm vm = new WebhookDetailVm();
        vm.setId(5L);
        vm.setPayloadUrl("https://example.com");
        vm.setSecret("secret");
        vm.setContentType("application/json");
        vm.setIsActive(true);
        vm.setEvents(List.of());

        assertEquals(5L, vm.getId());
        assertEquals("https://example.com", vm.getPayloadUrl());
        assertEquals("secret", vm.getSecret());
        assertEquals("application/json", vm.getContentType());
        assertTrue(vm.getIsActive());
    }

    // --- WebhookVm ---

    @Test
    void webhookVm_setterAndGetterShouldWork() {
        WebhookVm vm = new WebhookVm();
        vm.setId(1L);
        vm.setPayloadUrl("https://example.com");
        vm.setContentType("application/json");
        vm.setIsActive(true);

        assertEquals(1L, vm.getId());
        assertEquals("https://example.com", vm.getPayloadUrl());
        assertEquals("application/json", vm.getContentType());
        assertTrue(vm.getIsActive());
    }

    // --- WebhookListGetVm ---

    @Test
    void webhookListGetVm_setterAndGetterShouldWork() {
        WebhookListGetVm vm = new WebhookListGetVm();
        vm.setWebhooks(List.of());
        vm.setPageNo(2);
        vm.setPageSize(20);
        vm.setTotalElements(50);
        vm.setTotalPages(3);
        vm.setLast(true);

        assertEquals(2, vm.getPageNo());
        assertEquals(20, vm.getPageSize());
        assertEquals(50, vm.getTotalElements());
        assertEquals(3, vm.getTotalPages());
        assertTrue(vm.isLast());
    }

    // --- EventVm ---

    @Test
    void eventVm_builderShouldSetAllFields() {
        EventVm vm = EventVm.builder()
            .id(7L)
            .name(EventName.ON_ORDER_STATUS_UPDATED)
            .build();

        assertEquals(7L, vm.getId());
        assertEquals(EventName.ON_ORDER_STATUS_UPDATED, vm.getName());
    }

    @Test
    void eventVm_allArgsConstructorShouldWork() {
        EventVm vm = new EventVm(3L, EventName.ON_PRODUCT_UPDATED);

        assertEquals(3L, vm.getId());
        assertEquals(EventName.ON_PRODUCT_UPDATED, vm.getName());
    }

    @Test
    void eventVm_equalsAndHashCode_shouldWork() {
        EventVm vm1 = EventVm.builder().id(1L).name(EventName.ON_ORDER_CREATED).build();
        EventVm vm2 = EventVm.builder().id(1L).name(EventName.ON_ORDER_CREATED).build();
        EventVm vm3 = EventVm.builder().id(2L).name(EventName.ON_ORDER_CREATED).build();

        assertEquals(vm1, vm2);
        assertEquals(vm1.hashCode(), vm2.hashCode());
        assertNotEquals(vm1, vm3);
    }

    @Test
    void eventVm_noArgsConstructorShouldWork() {
        EventVm vm = new EventVm();
        vm.setId(10L);
        vm.setName(EventName.ON_ORDER_CREATED);

        assertEquals(10L, vm.getId());
        assertEquals(EventName.ON_ORDER_CREATED, vm.getName());
    }
}
