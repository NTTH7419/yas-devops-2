package com.yas.webhook.model.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EventNameTest {

    @Test
    void eventName_shouldHaveAllExpectedValues() {
        assertEquals(3, EventName.values().length);
        assertNotNull(EventName.ON_PRODUCT_UPDATED);
        assertNotNull(EventName.ON_ORDER_CREATED);
        assertNotNull(EventName.ON_ORDER_STATUS_UPDATED);
    }

    @Test
    void eventName_valueOf_shouldWork() {
        assertEquals(EventName.ON_PRODUCT_UPDATED, EventName.valueOf("ON_PRODUCT_UPDATED"));
        assertEquals(EventName.ON_ORDER_CREATED, EventName.valueOf("ON_ORDER_CREATED"));
        assertEquals(EventName.ON_ORDER_STATUS_UPDATED, EventName.valueOf("ON_ORDER_STATUS_UPDATED"));
    }

    @Test
    void eventName_toString_shouldReturnName() {
        assertEquals("ON_PRODUCT_UPDATED", EventName.ON_PRODUCT_UPDATED.toString());
        assertEquals("ON_ORDER_CREATED", EventName.ON_ORDER_CREATED.toString());
        assertEquals("ON_ORDER_STATUS_UPDATED", EventName.ON_ORDER_STATUS_UPDATED.toString());
    }
}
