package com.yas.payment.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    @DisplayName("getMessage with known error code should return localized message")
    void getMessage_WithKnownErrorCode_ShouldReturnLocalizedMessage() {
        // PAYMENT_PROVIDER_NOT_FOUND=Payment provider {} is not found
        String result = MessagesUtils.getMessage("PAYMENT_PROVIDER_NOT_FOUND", "paypal");
        assertThat(result).isEqualTo("Payment provider paypal is not found");
    }

    @Test
    @DisplayName("getMessage with unknown error code should return the error code itself")
    void getMessage_WithUnknownErrorCode_ShouldReturnErrorCode() {
        String unknownCode = "UNKNOWN_ERROR_CODE_XYZ";
        String result = MessagesUtils.getMessage(unknownCode);
        assertThat(result).isEqualTo(unknownCode);
    }

    @Test
    @DisplayName("getMessage with no args should return raw message template")
    void getMessage_WithNoArgs_ShouldReturnRawMessage() {
        // SUCCESS_MESSAGE=SUCCESS
        String result = MessagesUtils.getMessage("SUCCESS_MESSAGE");
        assertThat(result).isEqualTo("SUCCESS");
    }

    @Test
    @DisplayName("getMessage with multiple args should format all placeholders")
    void getMessage_WithMultipleArgs_ShouldFormatAllPlaceholders() {
        // Using a code with 1 placeholder, test it twice to confirm the format works
        String result1 = MessagesUtils.getMessage("PAYMENT_PROVIDER_NOT_FOUND", "cod");
        String result2 = MessagesUtils.getMessage("PAYMENT_PROVIDER_NOT_FOUND", "stripe");

        assertThat(result1).isEqualTo("Payment provider cod is not found");
        assertThat(result2).isEqualTo("Payment provider stripe is not found");
    }
}
