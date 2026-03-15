package com.yas.commonlibrary.model;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractAuditEntityTest {

    static class TestAuditEntity extends AbstractAuditEntity {}

    @Test
    void testAbstractAuditEntityPojo() {
        TestAuditEntity entity = new TestAuditEntity();
        ZonedDateTime now = ZonedDateTime.now();
        
        entity.setCreatedOn(now);
        entity.setCreatedBy("admin");
        entity.setLastModifiedOn(now);
        entity.setLastModifiedBy("editor");

        assertEquals(now, entity.getCreatedOn());
        assertEquals("admin", entity.getCreatedBy());
        assertEquals(now, entity.getLastModifiedOn());
        assertEquals("editor", entity.getLastModifiedBy());
    }
}
