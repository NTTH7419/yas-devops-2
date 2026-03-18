package com.yas.tax.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
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
import com.yas.tax.model.TaxRate;
import com.yas.tax.service.TaxRateService;
import com.yas.tax.viewmodel.taxrate.TaxRateGetDetailVm;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.List;
import org.instancio.Instancio;
import org.instancio.Select;
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
class TaxRateControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaxRateService taxRateService;

    @InjectMocks
    private TaxRateController taxRateController;

    private ObjectMapper objectMapper = new ObjectMapper();
    private TaxClass taxClass;
    private TaxRate taxRate;
    private TaxRateVm taxRateVm;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taxRateController).build();
        taxClass = Instancio.create(TaxClass.class);
        taxRate = Instancio.of(TaxRate.class)
            .set(Select.field("taxClass"), taxClass)
            .create();
        taxRateVm = TaxRateVm.fromModel(taxRate);
    }

    @Test
    void getPageableTaxRates_shouldReturnPagedTaxRates() throws Exception {
        // given
        TaxRateGetDetailVm detailVm = new TaxRateGetDetailVm(
            taxRate.getId(), taxRate.getRate(), taxRate.getZipCode(),
            taxClass.getName(), "State", "Country");
        TaxRateListGetVm listGetVm = new TaxRateListGetVm(
            List.of(detailVm), 0, 10, 1, 1, true);
        when(taxRateService.getPageableTaxRates(anyInt(), anyInt())).thenReturn(listGetVm);

        // when/then
        mockMvc.perform(get("/backoffice/tax-rates/paging")
                .param("pageNo", "0")
                .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taxRateGetDetailContent").isArray())
            .andExpect(jsonPath("$.pageNo").value(0));
    }

    @Test
    void getTaxRate_shouldReturnTaxRateWhenExists() throws Exception {
        // given
        when(taxRateService.findById(taxRate.getId())).thenReturn(taxRateVm);

        // when/then
        mockMvc.perform(get("/backoffice/tax-rates/{id}", taxRate.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(taxRate.getId()));
    }

    @Test
    void createTaxRate_shouldCreateAndReturn201() throws Exception {
        // given
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "12345", 1L, 1L, 1L);
        when(taxRateService.createTaxRate(any(TaxRatePostVm.class))).thenReturn(taxRate);

        // when/then
        mockMvc.perform(post("/backoffice/tax-rates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
            .andExpect(status().isCreated());
    }

    @Test
    void updateTaxRate_shouldUpdateAndReturn204() throws Exception {
        // given
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "12345", 1L, 1L, 1L);
        doNothing().when(taxRateService).updateTaxRate(any(TaxRatePostVm.class), anyLong());

        // when/then
        mockMvc.perform(put("/backoffice/tax-rates/{id}", taxRate.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
            .andExpect(status().isNoContent());
        verify(taxRateService).updateTaxRate(any(TaxRatePostVm.class), anyLong());
    }

    @Test
    void deleteTaxRate_shouldDeleteAndReturn204() throws Exception {
        // given
        doNothing().when(taxRateService).delete(anyLong());

        // when/then
        mockMvc.perform(delete("/backoffice/tax-rates/{id}", taxRate.getId()))
            .andExpect(status().isNoContent());
        verify(taxRateService).delete(taxRate.getId());
    }

    @Test
    void getTaxPercentByAddress_shouldReturnTaxPercent() throws Exception {
        // given
        Double expectedPercent = 10.5;
        when(taxRateService.getTaxPercent(anyLong(), anyLong(), anyLong(), anyString()))
            .thenReturn(expectedPercent);

        // when/then
        mockMvc.perform(get("/backoffice/tax-rates/tax-percent")
                .param("taxClassId", "1")
                .param("countryId", "1")
                .param("stateOrProvinceId", "1")
                .param("zipCode", "12345"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(expectedPercent));
    }

    @Test
    void getBatchTaxPercentsByAddress_shouldReturnTaxRates() throws Exception {
        // given
        lenient().when(taxRateService.getBulkTaxRate(any(), anyLong(), anyLong(), anyString()))
            .thenReturn(List.of(taxRateVm));

        // when/then
        mockMvc.perform(get("/backoffice/tax-rates/location-based-batch")
                .param("taxClassIds", "1,2")
                .param("countryId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }
}
