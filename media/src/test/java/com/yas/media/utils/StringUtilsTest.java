package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void hasText_whenStringHasText_thenReturnTrue() {
        assertTrue(StringUtils.hasText("text"));
        assertTrue(StringUtils.hasText("  text  "));
    }

    @Test
    void hasText_whenStringIsEmptyOrNull_thenReturnFalse() {
        assertFalse(StringUtils.hasText(""));
        assertFalse(StringUtils.hasText("   "));
        assertFalse(StringUtils.hasText(null));
    }
}
