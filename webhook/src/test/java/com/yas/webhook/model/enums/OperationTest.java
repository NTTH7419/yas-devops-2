package com.yas.webhook.model.enums;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OperationTest {

    @Test
    void operation_shouldHaveCorrectNameValues() {
        assertEquals("u", Operation.UPDATE.getName());
        assertEquals("c", Operation.CREATE.getName());
        assertEquals("d", Operation.DELETE.getName());
        assertEquals("r", Operation.READ.getName());
    }

    @Test
    void operation_shouldHaveAllExpectedValues() {
        assertEquals(4, Operation.values().length);
        assertNotNull(Operation.valueOf("UPDATE"));
        assertNotNull(Operation.valueOf("CREATE"));
        assertNotNull(Operation.valueOf("DELETE"));
        assertNotNull(Operation.valueOf("READ"));
    }

    @Test
    void operation_getName_shouldReturnString() {
        assertTrue(Operation.UPDATE.getName() instanceof String);
        assertEquals("u", Operation.UPDATE.getName());
    }
}
