package com.yas.webhook.model.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NotificationStatusTest {

    @Test
    void notificationStatus_shouldHaveCorrectValues() {
        assertEquals(2, NotificationStatus.values().length);
        assertNotNull(NotificationStatus.NOTIFYING);
        assertNotNull(NotificationStatus.NOTIFIED);
    }

    @Test
    void notificationStatus_valueOf_shouldWork() {
        assertEquals(NotificationStatus.NOTIFYING, NotificationStatus.valueOf("NOTIFYING"));
        assertEquals(NotificationStatus.NOTIFIED, NotificationStatus.valueOf("NOTIFIED"));
    }
}
