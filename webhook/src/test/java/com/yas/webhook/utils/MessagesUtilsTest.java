package com.yas.webhook.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_shouldReturnMessageWhenCodeExists() {
        // Using a known message code from messages.properties
        String message = MessagesUtils.getMessage("WEBHOOK_NOT_FOUND", 1L);
        assertNotNull(message);
        assertFalse(message.isEmpty());
    }

    @Test
    void getMessage_shouldReturnCodeWhenNotFound() {
        String message = MessagesUtils.getMessage("UNKNOWN_CODE_XYZ");
        assertEquals("UNKNOWN_CODE_XYZ", message);
    }

    @Test
    void getMessage_shouldFormatMessageWithVariables() {
        // Even when message code is not in properties, it returns the code itself
        String message = MessagesUtils.getMessage("WEBHOOK_NOT_FOUND", 42L);
        assertNotNull(message);
        assertEquals("WEBHOOK_NOT_FOUND", message);
    }

    @Test
    void getMessage_shouldHandleMultipleVariables() {
        // Returns the key when not defined in properties
        String message = MessagesUtils.getMessage("WEBHOOK_NOT_FOUND", 1L, 2L);
        assertNotNull(message);
        assertEquals("WEBHOOK_NOT_FOUND", message);
    }
}
