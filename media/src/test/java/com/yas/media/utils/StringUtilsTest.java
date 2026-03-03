package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void hasText_Null_ReturnsFalse() {
        assertFalse(StringUtils.hasText(null));
    }

    @Test
    void hasText_Empty_ReturnsFalse() {
        assertFalse(StringUtils.hasText(""));
    }

    @Test
    void hasText_OnlyWhitespace_ReturnsFalse() {
        assertFalse(StringUtils.hasText("   "));
        assertFalse(StringUtils.hasText("\t\n"));
    }

    @Test
    void hasText_WithText_ReturnsTrue() {
        assertTrue(StringUtils.hasText("text"));
        assertTrue(StringUtils.hasText("   text   "));
    }
}
