package com.yas.recommendation.vector.common.document;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultIdGeneratorTest {

    @Test
    void generateId_shouldReturnConsistentUuid() {
        String idPrefix = "product";
        Long identity = 123L;
        DefaultIdGenerator generator = new DefaultIdGenerator(idPrefix, identity);

        String id1 = generator.generateId();
        String id2 = generator.generateId();

        assertNotNull(id1);
        assertEquals(id1, id2);
        
        String expectedRawId = "product-123";
        String expectedUuid = UUID.nameUUIDFromBytes(expectedRawId.getBytes()).toString();
        assertEquals(expectedUuid, id1);
    }
}
