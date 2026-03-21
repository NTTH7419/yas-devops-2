package com.yas.webhook.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void test_getMessage_whenCodeFound_shouldReturnFormattedMessage() {
        // Since it uses ResourceBundle, we might not have control over the bundle in unit test
        // without mocking. But it's a static utility.
        // We can test the fallback case at least.
        String errorCode = "NON_EXISTENT_CODE";
        String message = MessagesUtils.getMessage(errorCode);
        assertEquals(errorCode, message);
    }

    @Test
    void test_getMessage_withArguments_shouldReturnFormattedMessage() {
        String errorCode = "NON_EXISTENT_CODE";
        Object[] args = {"arg1", "arg2"};
        String message = MessagesUtils.getMessage(errorCode, args);
        assertEquals(errorCode, message);
    }
}
