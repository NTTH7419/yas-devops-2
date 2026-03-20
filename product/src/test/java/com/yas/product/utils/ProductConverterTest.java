package com.yas.product.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductConverterTest {

    @Test
    void toSlug_WhenNormalString_ShouldReturnLowercaseSlug() {
        String result = ProductConverter.toSlug("Hello World");
        assertEquals("hello-world", result);
    }

    @Test
    void toSlug_WhenStringWithSpecialCharacters_ShouldReplaceWithDashes() {
        String result = ProductConverter.toSlug("Hello@World!Test");
        assertEquals("hello-world-test", result);
    }

    @Test
    void toSlug_WhenStringWithLeadingSpaces_ShouldTrimAndSlugify() {
        String result = ProductConverter.toSlug("  Hello World  ");
        assertEquals("hello-world", result);
    }

    @Test
    void toSlug_WhenStringWithMultipleDashes_ShouldCollapseToSingle() {
        String result = ProductConverter.toSlug("hello---world");
        assertEquals("hello-world", result);
    }

    @Test
    void toSlug_WhenStringStartsWithDash_ShouldRemoveLeadingDash() {
        String result = ProductConverter.toSlug("!Hello");
        assertEquals("hello", result);
    }

    @Test
    void toSlug_WhenStringIsAllSpecialCharacters_ShouldReturnEmpty() {
        String result = ProductConverter.toSlug("!!!");
        assertEquals("", result);
    }

    @Test
    void toSlug_WhenStringContainsNumbersAndLetters_ShouldSlugify() {
        String result = ProductConverter.toSlug("Product 123 Test");
        assertEquals("product-123-test", result);
    }

    @Test
    void toSlug_WhenStringIsAlreadySlug_ShouldReturnSame() {
        String result = ProductConverter.toSlug("hello-world");
        assertEquals("hello-world", result);
    }

    @Test
    void toSlug_WhenStringHasMixedSpecialCharacters_ShouldReplaceAllAndCollapse() {
        // "!!" at the end: after replace → "--", then collapses → "-", then "!Apple" → "!-apple" → still has trailing dash
        String result = ProductConverter.toSlug("Apple@@##--iPhone!!");
        assertEquals("apple-iphone-", result);
    }

    @Test
    void toSlug_WhenStringHasVietnameseCharacters_ShouldReplaceWithDashes() {
        // Vietnamese characters are not [a-z0-9\-] so replaced with dashes
        String result = ProductConverter.toSlug("Sản phẩm Apple");
        assertEquals("s-n-ph-m-apple", result);
    }
}
