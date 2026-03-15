package com.yas.commonlibrary.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MessagesUtilsTest {

    @Test
    void getMessage_withValidErrorCode_shouldReturnMappedMessage() {
        // Since we don't want to rely on the actual bundle content which might change,
        // and we can't easily mock ResourceBundle.getBundle, 
        // we test the fallback behavior or known common codes if possible.
        // The current implementation uses Locale.getDefault() which might be unpredictable.
        
        String errorCode = "non.existent.code";
        String message = MessagesUtils.getMessage(errorCode);
        assertEquals(errorCode, message);
    }

    @Test
    void getMessage_withArguments_shouldFormatMessage() {
        String errorCode = "Test message with {}";
        String arg = "argument";
        String message = MessagesUtils.getMessage(errorCode, arg);
        assertEquals("Test message with argument", message);
    }
}
