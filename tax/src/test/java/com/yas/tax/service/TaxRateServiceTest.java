package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.model.TaxClass;
import com.yas.tax.model.TaxRate;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.repository.TaxRateRepository;
import com.yas.tax.viewmodel.location.StateOrProvinceAndCountryGetNameVm;
import com.yas.tax.viewmodel.taxrate.TaxRateGetDetailVm;
import com.yas.tax.viewmodel.taxrate.TaxRateListGetVm;
import com.yas.tax.viewmodel.taxrate.TaxRatePostVm;
import com.yas.tax.viewmodel.taxrate.TaxRateVm;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class TaxRateServiceTest {

    @Mock
    private TaxRateRepository taxRateRepository;

    @Mock
    private TaxClassRepository taxClassRepository;

    @Mock
    private LocationService locationService;

    @InjectMocks
    private TaxRateService taxRateService;

    private TaxRate taxRate;
    private TaxClass taxClass;

    @BeforeEach
    void setUp() {
        taxClass = Instancio.create(TaxClass.class);
        taxRate = Instancio.of(TaxRate.class)
            .set(field("taxClass"), taxClass)
            .create();
    }

    @Test
    void createTaxRate_shouldCreateSuccessfully() {
        // given
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "12345", taxClass.getId(), 1L, 1L);
        TaxClass taxClassRef = TaxClass.builder().id(taxClass.getId()).name("Test").build();

        when(taxClassRepository.existsById(taxClass.getId())).thenReturn(true);
        when(taxClassRepository.getReferenceById(taxClass.getId())).thenReturn(taxClassRef);
        when(taxRateRepository.save(any(TaxRate.class))).thenReturn(taxRate);

        // when
        TaxRate result = taxRateService.createTaxRate(postVm);

        // then
        assertThat(result).isNotNull();
        verify(taxRateRepository).save(any(TaxRate.class));
    }

    @Test
    void createTaxRate_shouldThrowNotFoundExceptionWhenTaxClassNotExists() {
        // given
        Long nonExistentId = 999L;
        TaxRatePostVm postVm = new TaxRatePostVm(10.0, "12345", nonExistentId, 1L, 1L);

        when(taxClassRepository.existsById(nonExistentId)).thenReturn(false);

        // when/then
        assertThatThrownBy(() -> taxRateService.createTaxRate(postVm))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateTaxRate_shouldUpdateSuccessfully() {
        // given
        TaxRatePostVm postVm = new TaxRatePostVm(20.0, "54321", taxClass.getId(), 2L, 2L);
        TaxClass taxClassRef = TaxClass.builder().id(taxClass.getId()).name("Updated").build();

        when(taxRateRepository.findById(taxRate.getId())).thenReturn(Optional.of(taxRate));
        when(taxClassRepository.existsById(taxClass.getId())).thenReturn(true);
        when(taxClassRepository.getReferenceById(taxClass.getId())).thenReturn(taxClassRef);
        when(taxRateRepository.save(any(TaxRate.class))).thenReturn(taxRate);

        // when
        taxRateService.updateTaxRate(postVm, taxRate.getId());

        // then
        verify(taxRateRepository).findById(taxRate.getId());
        verify(taxRateRepository).save(any(TaxRate.class));
    }

    @Test
    void updateTaxRate_shouldThrowNotFoundExceptionWhenNotExists() {
        // given
        Long nonExistentId = 999L;
        TaxRatePostVm postVm = new TaxRatePostVm(20.0, "54321", taxClass.getId(), 2L, 2L);

        when(taxRateRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> taxRateService.updateTaxRate(postVm, nonExistentId))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateTaxRate_shouldThrowNotFoundExceptionWhenTaxClassNotExists() {
        // given
        Long nonExistentTaxClassId = 999L;
        TaxRatePostVm postVm = new TaxRatePostVm(20.0, "54321", nonExistentTaxClassId, 2L, 2L);

        when(taxRateRepository.findById(taxRate.getId())).thenReturn(Optional.of(taxRate));
        when(taxClassRepository.existsById(nonExistentTaxClassId)).thenReturn(false);

        // when/then
        assertThatThrownBy(() -> taxRateService.updateTaxRate(postVm, taxRate.getId()))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void delete_shouldDeleteSuccessfully() {
        // given
        when(taxRateRepository.existsById(taxRate.getId())).thenReturn(true);
        doNothing().when(taxRateRepository).deleteById(taxRate.getId());

        // when
        taxRateService.delete(taxRate.getId());

        // then
        verify(taxRateRepository).existsById(taxRate.getId());
        verify(taxRateRepository).deleteById(taxRate.getId());
    }

    @Test
    void delete_shouldThrowNotFoundExceptionWhenNotExists() {
        // given
        Long nonExistentId = 999L;
        when(taxRateRepository.existsById(nonExistentId)).thenReturn(false);

        // when/then
        assertThatThrownBy(() -> taxRateService.delete(nonExistentId))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findById_shouldReturnTaxRateWhenExists() {
        // given
        when(taxRateRepository.findById(taxRate.getId())).thenReturn(Optional.of(taxRate));

        // when
        TaxRateVm result = taxRateService.findById(taxRate.getId());

        // then
        assertThat(result.id()).isEqualTo(taxRate.getId());
        assertThat(result.rate()).isEqualTo(taxRate.getRate());
    }

    @Test
    void findById_shouldThrowNotFoundExceptionWhenNotExists() {
        // given
        Long nonExistentId = 999L;
        when(taxRateRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> taxRateService.findById(nonExistentId))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void findAll_shouldReturnAllTaxRates() {
        // given
        when(taxRateRepository.findAll()).thenReturn(List.of(taxRate));

        // when
        List<TaxRateVm> result = taxRateService.findAll();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(taxRate.getId());
    }

    @Test
    void findAll_shouldReturnEmptyList() {
        // given
        when(taxRateRepository.findAll()).thenReturn(List.of());

        // when
        List<TaxRateVm> result = taxRateService.findAll();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void getPageableTaxRates_shouldReturnPagedTaxRatesWithLocation() {
        // given
        int pageNo = 0;
        int pageSize = 10;
        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        PageImpl<TaxRate> page = new PageImpl<>(List.of(taxRate), pageable, 1);

        List<StateOrProvinceAndCountryGetNameVm> locationVms = List.of(
            new StateOrProvinceAndCountryGetNameVm(
                taxRate.getStateOrProvinceId(),
                "State Name",
                "Country Name"
            )
        );

        when(taxRateRepository.findAll(pageable)).thenReturn(page);
        when(locationService.getStateOrProvinceAndCountryNames(any()))
            .thenReturn(locationVms);

        // when
        TaxRateListGetVm result = taxRateService.getPageableTaxRates(pageNo, pageSize);

        // then
        assertThat(result.taxRateGetDetailContent()).hasSize(1);
        assertThat(result.pageNo()).isEqualTo(pageNo);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void getPageableTaxRates_shouldReturnPagedTaxRatesWithoutLocation() {
        // given
        int pageNo = 0;
        int pageSize = 10;
        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        PageImpl<TaxRate> page = new PageImpl<>(List.of(taxRate), pageable, 1);

        when(taxRateRepository.findAll(pageable)).thenReturn(page);
        when(locationService.getStateOrProvinceAndCountryNames(any()))
            .thenReturn(Collections.emptyList());

        // when
        TaxRateListGetVm result = taxRateService.getPageableTaxRates(pageNo, pageSize);

        // then
        assertThat(result.taxRateGetDetailContent()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void getPageableTaxRates_shouldReturnEmptyPage() {
        // given
        int pageNo = 0;
        int pageSize = 10;
        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        PageImpl<TaxRate> page = new PageImpl<>(List.of(), pageable, 0);

        when(taxRateRepository.findAll(pageable)).thenReturn(page);

        // when
        TaxRateListGetVm result = taxRateService.getPageableTaxRates(pageNo, pageSize);

        // then
        assertThat(result.taxRateGetDetailContent()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0);
        assertThat(result.totalPages()).isEqualTo(0);
    }

    @Test
    void getTaxPercent_shouldReturnTaxPercentWhenFound() {
        // given
        Double expectedPercent = 10.5;
        when(taxRateRepository.getTaxPercent(1L, 1L, "12345", taxClass.getId()))
            .thenReturn(expectedPercent);

        // when
        double result = taxRateService.getTaxPercent(taxClass.getId(), 1L, 1L, "12345");

        // then
        assertThat(result).isEqualTo(expectedPercent);
    }

    @Test
    void getTaxPercent_shouldReturnZeroWhenNotFound() {
        // given
        when(taxRateRepository.getTaxPercent(any(), any(), anyString(), any()))
            .thenReturn(null);

        // when
        double result = taxRateService.getTaxPercent(taxClass.getId(), 1L, 1L, "12345");

        // then
        assertThat(result).isEqualTo(0);
    }

    @Test
    void getBulkTaxRate_shouldReturnTaxRates() {
        // given
        List<Long> taxClassIds = List.of(taxClass.getId());
        List<TaxRate> taxRates = List.of(taxRate);

        when(taxRateRepository.getBatchTaxRates(1L, 1L, "12345", new HashSet<>(taxClassIds)))
            .thenReturn(taxRates);

        // when
        List<TaxRateVm> result = taxRateService.getBulkTaxRate(taxClassIds, 1L, 1L, "12345");

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void getBulkTaxRate_shouldReturnEmptyList() {
        // given
        List<Long> taxClassIds = List.of(taxClass.getId());

        when(taxRateRepository.getBatchTaxRates(any(), any(), anyString(), anySet()))
            .thenReturn(Collections.emptyList());

        // when
        List<TaxRateVm> result = taxRateService.getBulkTaxRate(taxClassIds, 1L, 1L, "12345");

        // then
        assertThat(result).isEmpty();
    }
}
