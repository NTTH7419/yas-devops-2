package com.yas.webhook.utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.Test;

class HmacUtilsTest {

    @Test
    void test_hash_shouldReturnHash() throws NoSuchAlgorithmException, InvalidKeyException {
        String data = "test-data";
        String key = "test-key";
        String hash = HmacUtils.hash(data, key);
        assertNotNull(hash);
    }

    @Test
    void test_hash_withEmptyData_shouldReturnHash() throws NoSuchAlgorithmException, InvalidKeyException {
        String data = "";
        String key = "test-key";
        String hash = HmacUtils.hash(data, key);
        assertNotNull(hash);
    }
}
