package com.yas.commonlibrary.csv;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseCsvTest {

    @Test
    void testBaseCsvPojo() {
        BaseCsv baseCsv = BaseCsv.builder()
                .id(123L)
                .build();

        assertEquals(123L, baseCsv.getId());

        baseCsv.setId(456L);
        assertEquals(456L, baseCsv.getId());
    }
}
