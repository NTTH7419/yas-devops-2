package com.yas.commonlibrary.viewmodel.error;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorVmTest {

    @Test
    void testErrorVm() {
        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Detail");
        assertEquals("400", errorVm.statusCode());
        assertEquals("Bad Request", errorVm.title());
        assertEquals("Detail", errorVm.detail());
        assertTrue(errorVm.fieldErrors().isEmpty());

        ErrorVm errorVmWithFields = new ErrorVm("400", "Bad Request", "Detail", List.of("error1"));
        assertEquals(1, errorVmWithFields.fieldErrors().size());
        assertEquals("error1", errorVmWithFields.fieldErrors().get(0));
    }
}
