package com.yas.webhook.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.Test;

class HmacUtilsTest {

    @Test
    void testHash_shouldReturnHmacSha256Signature() throws Exception {
        String data = "{\"key\":\"value\"}";
        String key = "my-secret-key";

        String result = HmacUtils.hash(data, key);

        assertNotNull(result);
        // HMAC output should be consistent for same input
        assertEquals(result, HmacUtils.hash(data, key));
    }

    @Test
    void testHash_withDifferentKeys_shouldReturnDifferentSignatures() throws Exception {
        String data = "{\"key\":\"value\"}";

        String result1 = HmacUtils.hash(data, "key1");
        String result2 = HmacUtils.hash(data, "key2");

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotEquals(result1, result2);
    }

    @Test
    void testHash_withEmptyData_shouldReturnSignature() throws Exception {
        String data = "";
        String key = "secret";

        String result = HmacUtils.hash(data, key);

        assertNotNull(result);
    }

    @Test
    void testHash_shouldUseHmacSha256Algorithm() {
        assertEquals("HmacSHA256", HmacUtils.HMAC_SHA_256);
    }

    @Test
    void testHash_withSpecialCharacters_shouldReturnSignature() throws Exception {
        String data = "{\"name\":\"John \\\"Doe\\\"\",\"age\":30}";
        String key = "special-key-123!@#";

        String result = HmacUtils.hash(data, key);

        assertNotNull(result);
        assertEquals(result, HmacUtils.hash(data, key));
    }

    @Test
    void testHash_withLongData_shouldReturnSignature() throws Exception {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("{\"index\":").append(i).append("},");
        }
        String data = sb.toString();
        String key = "long-data-key";

        String result = HmacUtils.hash(data, key);

        assertNotNull(result);
    }
}
