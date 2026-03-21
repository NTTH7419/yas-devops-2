package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.model.TaxClass;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
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
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class TaxClassServiceTest {

    @Mock
    private TaxClassRepository taxClassRepository;

    @InjectMocks
    private TaxClassService taxClassService;

    private TaxClass taxClass;

    @BeforeEach
    void setUp() {
        taxClass = Instancio.create(TaxClass.class);
    }

    @Test
    void findAllTaxClasses_shouldReturnAllTaxClasses() {
        // given
        when(taxClassRepository.findAll(Sort.by(Sort.Direction.ASC, "name")))
            .thenReturn(List.of(taxClass));

        // when
        List<TaxClassVm> result = taxClassService.findAllTaxClasses();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(taxClass.getId());
        assertThat(result.get(0).name()).isEqualTo(taxClass.getName());
    }

    @Test
    void findAllTaxClasses_shouldReturnEmptyList() {
        // given
        when(taxClassRepository.findAll(Sort.by(Sort.Direction.ASC, "name")))
            .thenReturn(List.of());

        // when
        List<TaxClassVm> result = taxClassService.findAllTaxClasses();

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void findById_shouldReturnTaxClassWhenExists() {
        // given
        when(taxClassRepository.findById(taxClass.getId()))
            .thenReturn(Optional.of(taxClass));

        // when
        TaxClassVm result = taxClassService.findById(taxClass.getId());

        // then
        assertThat(result.id()).isEqualTo(taxClass.getId());
        assertThat(result.name()).isEqualTo(taxClass.getName());
    }

    @Test
    void findById_shouldThrowNotFoundExceptionWhenNotExists() {
        // given
        Long nonExistentId = 999L;
        when(taxClassRepository.findById(nonExistentId))
            .thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> taxClassService.findById(nonExistentId))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_shouldCreateTaxClassSuccessfully() {
        // given
        TaxClassPostVm postVm = new TaxClassPostVm(null, "New Tax Class");
        TaxClass savedTaxClass = TaxClass.builder()
            .id(1L)
            .name("New Tax Class")
            .build();

        when(taxClassRepository.existsByName("New Tax Class")).thenReturn(false);
        when(taxClassRepository.save(any(TaxClass.class))).thenReturn(savedTaxClass);

        // when
        TaxClass result = taxClassService.create(postVm);

        // then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("New Tax Class");
        verify(taxClassRepository).save(any(TaxClass.class));
    }

    @Test
    void create_shouldThrowDuplicatedExceptionWhenNameExists() {
        // given
        TaxClassPostVm postVm = new TaxClassPostVm(null, "Existing Tax Class");
        when(taxClassRepository.existsByName("Existing Tax Class")).thenReturn(true);

        // when/then
        assertThatThrownBy(() -> taxClassService.create(postVm))
            .isInstanceOf(DuplicatedException.class);
        verify(taxClassRepository).existsByName("Existing Tax Class");
    }

    @Test
    void update_shouldUpdateTaxClassSuccessfully() {
        // given
        TaxClassPostVm postVm = new TaxClassPostVm(null, "Updated Tax Class");
        when(taxClassRepository.findById(taxClass.getId())).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("Updated Tax Class", taxClass.getId()))
            .thenReturn(false);
        when(taxClassRepository.save(any(TaxClass.class))).thenReturn(taxClass);

        // when
        taxClassService.update(postVm, taxClass.getId());

        // then
        verify(taxClassRepository).findById(taxClass.getId());
        verify(taxClassRepository).existsByNameNotUpdatingTaxClass("Updated Tax Class", taxClass.getId());
        verify(taxClassRepository).save(any(TaxClass.class));
    }

    @Test
    void update_shouldThrowNotFoundExceptionWhenNotExists() {
        // given
        Long nonExistentId = 999L;
        TaxClassPostVm postVm = new TaxClassPostVm(null, "Updated Tax Class");
        when(taxClassRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> taxClassService.update(postVm, nonExistentId))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_shouldThrowDuplicatedExceptionWhenNameExists() {
        // given
        TaxClassPostVm postVm = new TaxClassPostVm(null, "Existing Name");
        when(taxClassRepository.findById(taxClass.getId())).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass("Existing Name", taxClass.getId()))
            .thenReturn(true);

        // when/then
        assertThatThrownBy(() -> taxClassService.update(postVm, taxClass.getId()))
            .isInstanceOf(DuplicatedException.class);
    }

    @Test
    void delete_shouldDeleteTaxClassSuccessfully() {
        // given
        when(taxClassRepository.existsById(taxClass.getId())).thenReturn(true);
        doNothing().when(taxClassRepository).deleteById(taxClass.getId());

        // when
        taxClassService.delete(taxClass.getId());

        // then
        verify(taxClassRepository).existsById(taxClass.getId());
        verify(taxClassRepository).deleteById(taxClass.getId());
    }

    @Test
    void delete_shouldThrowNotFoundExceptionWhenNotExists() {
        // given
        Long nonExistentId = 999L;
        when(taxClassRepository.existsById(nonExistentId)).thenReturn(false);

        // when/then
        assertThatThrownBy(() -> taxClassService.delete(nonExistentId))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getPageableTaxClasses_shouldReturnPagedTaxClasses() {
        // given
        int pageNo = 0;
        int pageSize = 10;
        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        PageImpl<TaxClass> page = new PageImpl<>(List.of(taxClass), pageable, 1);

        when(taxClassRepository.findAll(pageable)).thenReturn(page);

        // when
        TaxClassListGetVm result = taxClassService.getPageableTaxClasses(pageNo, pageSize);

        // then
        assertThat(result.taxClassContent()).hasSize(1);
        assertThat(result.pageNo()).isEqualTo(pageNo);
        assertThat(result.pageSize()).isEqualTo(pageSize);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
    }

    @Test
    void getPageableTaxClasses_shouldReturnEmptyPage() {
        // given
        int pageNo = 0;
        int pageSize = 10;
        PageRequest pageable = PageRequest.of(pageNo, pageSize);
        PageImpl<TaxClass> page = new PageImpl<>(List.of(), pageable, 0);

        when(taxClassRepository.findAll(pageable)).thenReturn(page);

        // when
        TaxClassListGetVm result = taxClassService.getPageableTaxClasses(pageNo, pageSize);

        // then
        assertThat(result.taxClassContent()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0);
        assertThat(result.totalPages()).isEqualTo(0);
    }
}
