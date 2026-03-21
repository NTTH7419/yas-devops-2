package com.yas.tax.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.tax.model.TaxClass;
import com.yas.tax.service.TaxClassService;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
import java.util.List;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class TaxClassControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaxClassService taxClassService;

    @InjectMocks
    private TaxClassController taxClassController;

    private ObjectMapper objectMapper = new ObjectMapper();
    private TaxClass taxClass;
    private TaxClassVm taxClassVm;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taxClassController).build();
        taxClass = Instancio.create(TaxClass.class);
        taxClassVm = TaxClassVm.fromModel(taxClass);
    }

    @Test
    void getPageableTaxClasses_shouldReturnPagedTaxClasses() throws Exception {
        // given
        TaxClassListGetVm listGetVm = new TaxClassListGetVm(
            List.of(taxClassVm), 0, 10, 1, 1, true);
        when(taxClassService.getPageableTaxClasses(anyInt(), anyInt())).thenReturn(listGetVm);

        // when/then
        mockMvc.perform(get("/backoffice/tax-classes/paging")
                .param("pageNo", "0")
                .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taxClassContent").isArray())
            .andExpect(jsonPath("$.pageNo").value(0));
    }

    @Test
    void listTaxClasses_shouldReturnAllTaxClasses() throws Exception {
        // given
        when(taxClassService.findAllTaxClasses()).thenReturn(List.of(taxClassVm));

        // when/then
        mockMvc.perform(get("/backoffice/tax-classes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].id").value(taxClass.getId()));
    }

    @Test
    void getTaxClass_shouldReturnTaxClassWhenExists() throws Exception {
        // given
        when(taxClassService.findById(taxClass.getId())).thenReturn(taxClassVm);

        // when/then
        mockMvc.perform(get("/backoffice/tax-classes/{id}", taxClass.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(taxClass.getId()))
            .andExpect(jsonPath("$.name").value(taxClass.getName()));
    }

    @Test
    void createTaxClass_shouldCreateAndReturn201() throws Exception {
        // given
        TaxClassPostVm postVm = new TaxClassPostVm("1", "New Tax Class");
        TaxClass savedTaxClass = TaxClass.builder().id(1L).name("New Tax Class").build();

        when(taxClassService.create(any(TaxClassPostVm.class))).thenReturn(savedTaxClass);

        // when/then
        mockMvc.perform(post("/backoffice/tax-classes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("New Tax Class"));
    }

    @Test
    void updateTaxClass_shouldUpdateAndReturn204() throws Exception {
        // given
        TaxClassPostVm postVm = new TaxClassPostVm("1", "Updated Tax Class");
        doNothing().when(taxClassService).update(any(TaxClassPostVm.class), any(Long.class));

        // when/then
        mockMvc.perform(put("/backoffice/tax-classes/{id}", taxClass.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
            .andExpect(status().isNoContent());
        verify(taxClassService).update(any(TaxClassPostVm.class), any(Long.class));
    }

    @Test
    void deleteTaxClass_shouldDeleteAndReturn204() throws Exception {
        // given
        doNothing().when(taxClassService).delete(any(Long.class));

        // when/then
        mockMvc.perform(delete("/backoffice/tax-classes/{id}", taxClass.getId()))
            .andExpect(status().isNoContent());
        verify(taxClassService).delete(taxClass.getId());
    }
}
