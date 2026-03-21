package com.yas.webhook.model.viewmodel.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.Test;

class ErrorVmTest {

    @Test
    void test_errorVm_constructor() {
        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Detail");
        assertEquals("400", errorVm.statusCode());
        assertEquals("Bad Request", errorVm.title());
        assertEquals("Detail", errorVm.detail());
        assertNotNull(errorVm.fieldErrors());
    }

    @Test
    void test_errorVm_fullConstructor() {
        List<String> fieldErrors = List.of("error1");
        ErrorVm errorVm = new ErrorVm("400", "Bad Request", "Detail", fieldErrors);
        assertEquals(fieldErrors, errorVm.fieldErrors());
    }
}
