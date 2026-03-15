package com.yas.commonlibrary.kafka.cdc.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CdcMessageTests {

    @Test
    void testProductPojo() {
        Product product = Product.builder()
                .id(1L)
                .isPublished(true)
                .build();

        assertEquals(1L, product.getId());
        assertTrue(product.isPublished());

        product.setId(2L);
        product.setPublished(false);

        assertEquals(2L, product.getId());
        assertFalse(product.isPublished());
    }

    @Test
    void testProductMsgKeyPojo() {
        ProductMsgKey key = ProductMsgKey.builder()
                .id(100L)
                .build();

        assertEquals(100L, key.getId());

        key.setId(200L);
        assertEquals(200L, key.getId());
    }

    @Test
    void testOperationEnum() {
        assertEquals("r", Operation.READ.getName());
        assertEquals("c", Operation.CREATE.getName());
        assertEquals("u", Operation.UPDATE.getName());
        assertEquals("d", Operation.DELETE.getName());
    }

    @Test
    void testProductCdcMessagePojo() {
        Product before = Product.builder().id(1L).isPublished(false).build();
        Product after = Product.builder().id(1L).isPublished(true).build();
        
        ProductCdcMessage message = ProductCdcMessage.builder()
                .before(before)
                .after(after)
                .op(Operation.UPDATE)
                .build();

        assertEquals(before, message.getBefore());
        assertEquals(after, message.getAfter());
        assertEquals(Operation.UPDATE, message.getOp());

        message.setOp(Operation.DELETE);
        assertEquals(Operation.DELETE, message.getOp());
    }
}
