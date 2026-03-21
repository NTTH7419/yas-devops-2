package com.yas.inventory.service;

import static com.yas.inventory.util.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.model.enumeration.FilterExistInWhSelection;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.address.AddressDetailVm;
import com.yas.inventory.viewmodel.address.AddressPostVm;
import com.yas.inventory.viewmodel.address.AddressVm;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseDetailVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehouseListGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehousePostVm;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class WarehouseServiceTest {

    private WarehouseRepository warehouseRepository;
    private StockRepository stockRepository;
    private ProductService productService;
    private LocationService locationService;
    private WarehouseService warehouseService;

    @BeforeEach
    void setUp() {
        warehouseRepository = mock(WarehouseRepository.class);
        stockRepository = mock(StockRepository.class);
        productService = mock(ProductService.class);
        locationService = mock(LocationService.class);
        warehouseService = new WarehouseService(
            warehouseRepository,
            stockRepository,
            productService,
            locationService
        );
        setUpSecurityContext("test-user");
    }

    // ============= findAllWarehouses tests =============

    @Nested
    class FindAllWarehousesTests {

        @Test
        void findAllWarehouses_shouldReturnAllWarehouses() {
            Warehouse warehouse1 = Warehouse.builder().id(1L).name("Warehouse A").addressId(100L).build();
            Warehouse warehouse2 = Warehouse.builder().id(2L).name("Warehouse B").addressId(200L).build();

            when(warehouseRepository.findAll()).thenReturn(List.of(warehouse1, warehouse2));

            List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

            assertEquals(2, result.size());
            assertEquals("Warehouse A", result.get(0).name());
            assertEquals("Warehouse B", result.get(1).name());
        }

        @Test
        void findAllWarehouses_shouldReturnEmptyList_whenNoWarehouses() {
            when(warehouseRepository.findAll()).thenReturn(Collections.emptyList());

            List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

            assertEquals(0, result.size());
        }
    }

    // ============= getProductWarehouse tests =============

    @Nested
    class GetProductWarehouseTests {

        @Test
        void getProductWarehouse_shouldReturnProductsWithExistStatus_whenProductIdsNotEmpty() {
            Long warehouseId = 1L;
            List<Long> productIds = List.of(10L, 20L);

            ProductInfoVm product1 = new ProductInfoVm(10L, "Product A", "SKU-A", true);
            ProductInfoVm product2 = new ProductInfoVm(20L, "Product B", "SKU-B", true);

            when(stockRepository.getProductIdsInWarehouse(warehouseId)).thenReturn(productIds);
            when(productService.filterProducts(any(), any(), anyList(), any()))
                .thenReturn(List.of(product1, product2));

            List<ProductInfoVm> result = warehouseService.getProductWarehouse(
                warehouseId, null, null, FilterExistInWhSelection.YES);

            assertEquals(2, result.size());
            verify(productService, times(1)).filterProducts(null, null, productIds, FilterExistInWhSelection.YES);
        }

        @Test
        void getProductWarehouse_shouldReturnProductsWithExistStatusSet_whenProductIdsNotEmpty() {
            Long warehouseId = 1L;
            List<Long> productIds = List.of(10L, 20L);

            ProductInfoVm product1 = new ProductInfoVm(10L, "Product A", "SKU-A", true);
            ProductInfoVm product2 = new ProductInfoVm(20L, "Product B", "SKU-B", true);

            when(stockRepository.getProductIdsInWarehouse(warehouseId)).thenReturn(productIds);
            when(productService.filterProducts(any(), any(), anyList(), any()))
                .thenReturn(List.of(product1, product2));

            List<ProductInfoVm> result = warehouseService.getProductWarehouse(
                warehouseId, "name", "sku", FilterExistInWhSelection.YES);

            for (ProductInfoVm p : result) {
                assertEquals(true, productIds.contains(p.id()));
            }
        }

        @Test
        void getProductWarehouse_shouldReturnDirectList_whenProductIdsEmpty() {
            Long warehouseId = 1L;

            ProductInfoVm product1 = new ProductInfoVm(10L, "Product A", "SKU-A", true);

            when(stockRepository.getProductIdsInWarehouse(warehouseId)).thenReturn(Collections.emptyList());
            when(productService.filterProducts(any(), any(), anyList(), any()))
                .thenReturn(List.of(product1));

            List<ProductInfoVm> result = warehouseService.getProductWarehouse(
                warehouseId, "name", "sku", FilterExistInWhSelection.YES);

            assertEquals(1, result.size());
        }

        @Test
        void getProductWarehouse_shouldReturnEmptyList_whenNoProductIdsAndNoFilterResults() {
            Long warehouseId = 1L;

            when(stockRepository.getProductIdsInWarehouse(warehouseId)).thenReturn(Collections.emptyList());
            when(productService.filterProducts(any(), any(), anyList(), any()))
                .thenReturn(Collections.emptyList());

            List<ProductInfoVm> result = warehouseService.getProductWarehouse(
                warehouseId, "NonExistent", "NONEXISTENT", FilterExistInWhSelection.YES);

            assertEquals(0, result.size());
        }
    }

    // ============= findById tests =============

    @Nested
    class FindByIdTests {

        @Test
        void findById_shouldReturnWarehouseDetailVm_whenWarehouseExists() {
            Long warehouseId = 1L;

            Warehouse warehouse = new Warehouse();
            warehouse.setId(warehouseId);
            warehouse.setName("Main Warehouse");
            warehouse.setAddressId(500L);

            AddressDetailVm addressDetailVm = AddressDetailVm.builder()
                .id(500L)
                .contactName("John Doe")
                .phone("123-456-7890")
                .addressLine1("123 Main St")
                .addressLine2("Suite 100")
                .city("Springfield")
                .zipCode("12345")
                .districtId(10L)
                .stateOrProvinceId(5L)
                .countryId(2L)
                .build();

            when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
            when(locationService.getAddressById(500L)).thenReturn(addressDetailVm);

            WarehouseDetailVm result = warehouseService.findById(warehouseId);

            assertEquals(warehouseId, result.id());
            assertEquals("Main Warehouse", result.name());
            assertEquals("John Doe", result.contactName());
            assertEquals("123-456-7890", result.phone());
            assertEquals("123 Main St", result.addressLine1());
            assertEquals("Suite 100", result.addressLine2());
            assertEquals("Springfield", result.city());
            assertEquals("12345", result.zipCode());
            assertEquals(10L, result.districtId());
            assertEquals(5L, result.stateOrProvinceId());
            assertEquals(2L, result.countryId());
        }

        @Test
        void findById_shouldThrowNotFoundException_whenWarehouseNotFound() {
            Long warehouseId = 999L;

            when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

            NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                warehouseService.findById(warehouseId)
            );

            assertThat(thrown.getMessage()).contains("not found");
        }
    }

    // ============= create tests =============

    @Nested
    class CreateTests {

        @Test
        void create_shouldCreateWarehouse_whenNameNotDuplicate() {
            WarehousePostVm warehousePostVm = WarehousePostVm.builder()
                .name("New Warehouse")
                .contactName("Jane Doe")
                .phone("555-1234")
                .addressLine1("456 Oak St")
                .addressLine2("Apt 2")
                .city("Metropolis")
                .zipCode("54321")
                .districtId(1L)
                .stateOrProvinceId(2L)
                .countryId(3L)
                .build();

            AddressVm addressVm = new AddressVm(
                100L, "Jane Doe", "555-1234", "456 Oak St", "Metropolis", "54321",
                1L, 2L, 3L
            );

            Warehouse savedWarehouse = Warehouse.builder()
                .id(1L)
                .name("New Warehouse")
                .addressId(100L)
                .build();

            when(warehouseRepository.existsByName("New Warehouse")).thenReturn(false);
            when(locationService.createAddress(any(AddressPostVm.class))).thenReturn(addressVm);
            when(warehouseRepository.save(any(Warehouse.class))).thenReturn(savedWarehouse);

            Warehouse result = warehouseService.create(warehousePostVm);

            assertEquals(1L, result.getId());
            assertEquals("New Warehouse", result.getName());
            assertEquals(100L, result.getAddressId());
        }

        @Test
        void create_shouldThrowDuplicatedException_whenNameAlreadyExists() {
            WarehousePostVm warehousePostVm = WarehousePostVm.builder()
                .name("Existing Warehouse")
                .contactName("Jane Doe")
                .phone("555-1234")
                .addressLine1("456 Oak St")
                .city("Metropolis")
                .zipCode("54321")
                .districtId(1L)
                .stateOrProvinceId(2L)
                .countryId(3L)
                .build();

            when(warehouseRepository.existsByName("Existing Warehouse")).thenReturn(true);

            DuplicatedException thrown = assertThrows(DuplicatedException.class, () ->
                warehouseService.create(warehousePostVm)
            );

            assertThat(thrown.getMessage()).contains("already");
        }

        @Test
        void create_shouldBuildAddressPostVmCorrectly() {
            WarehousePostVm warehousePostVm = WarehousePostVm.builder()
                .name("New Warehouse")
                .contactName("Jane Doe")
                .phone("555-1234")
                .addressLine1("456 Oak St")
                .addressLine2("Suite 2")
                .city("Metropolis")
                .zipCode("54321")
                .districtId(1L)
                .stateOrProvinceId(2L)
                .countryId(3L)
                .build();

            AddressVm addressVm = new AddressVm(100L, "Jane Doe", "555-1234", "456 Oak St", "Metropolis", "54321",
                1L, 2L, 3L);
            Warehouse savedWarehouse = Warehouse.builder().id(1L).name("New Warehouse").addressId(100L).build();

            when(warehouseRepository.existsByName(any())).thenReturn(false);
            when(locationService.createAddress(any(AddressPostVm.class))).thenReturn(addressVm);
            when(warehouseRepository.save(any(Warehouse.class))).thenReturn(savedWarehouse);

            warehouseService.create(warehousePostVm);

            ArgumentCaptor<AddressPostVm> captor = ArgumentCaptor.forClass(AddressPostVm.class);
            verify(locationService, times(1)).createAddress(captor.capture());

            AddressPostVm captured = captor.getValue();
            assertEquals("Jane Doe", captured.contactName());
            assertEquals("555-1234", captured.phone());
            assertEquals("456 Oak St", captured.addressLine1());
            assertEquals("Suite 2", captured.addressLine2());
            assertEquals("Metropolis", captured.city());
            assertEquals("54321", captured.zipCode());
            assertEquals(1L, captured.districtId());
            assertEquals(2L, captured.stateOrProvinceId());
            assertEquals(3L, captured.countryId());
        }
    }

    // ============= update tests =============

    @Nested
    class UpdateTests {

        @Test
        void update_shouldUpdateWarehouse_whenRequestValid() {
            Long warehouseId = 1L;

            Warehouse existingWarehouse = Warehouse.builder()
                .id(warehouseId)
                .name("Old Name")
                .addressId(100L)
                .build();

            WarehousePostVm warehousePostVm = WarehousePostVm.builder()
                .name("Updated Warehouse")
                .contactName("Updated Contact")
                .phone("999-9999")
                .addressLine1("789 Pine St")
                .addressLine2("Floor 3")
                .city("New City")
                .zipCode("99999")
                .districtId(5L)
                .stateOrProvinceId(6L)
                .countryId(7L)
                .build();

            when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(existingWarehouse));
            when(warehouseRepository.existsByNameWithDifferentId("Updated Warehouse", warehouseId)).thenReturn(false);
            doNothing().when(locationService).updateAddress(anyLong(), any(AddressPostVm.class));
            when(warehouseRepository.save(any(Warehouse.class))).thenAnswer(inv -> inv.getArguments()[0]);

            warehouseService.update(warehousePostVm, warehouseId);

            assertEquals("Updated Warehouse", existingWarehouse.getName());
            verify(locationService, times(1)).updateAddress(eq(100L), any(AddressPostVm.class));
            verify(warehouseRepository, times(1)).save(existingWarehouse);
        }

        @Test
        void update_shouldThrowNotFoundException_whenWarehouseNotFound() {
            Long warehouseId = 999L;

            WarehousePostVm warehousePostVm = WarehousePostVm.builder()
                .name("Updated Warehouse")
                .contactName("Contact")
                .phone("999-9999")
                .addressLine1("789 Pine St")
                .city("New City")
                .zipCode("99999")
                .districtId(5L)
                .stateOrProvinceId(6L)
                .countryId(7L)
                .build();

            when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

            NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                warehouseService.update(warehousePostVm, warehouseId)
            );

            assertThat(thrown.getMessage()).contains("not found");
        }

        @Test
        void update_shouldThrowDuplicatedException_whenNameAlreadyExists() {
            Long warehouseId = 1L;

            Warehouse existingWarehouse = Warehouse.builder()
                .id(warehouseId)
                .name("Old Name")
                .addressId(100L)
                .build();

            WarehousePostVm warehousePostVm = WarehousePostVm.builder()
                .name("Duplicate Name")
                .contactName("Contact")
                .phone("999-9999")
                .addressLine1("789 Pine St")
                .city("New City")
                .zipCode("99999")
                .districtId(5L)
                .stateOrProvinceId(6L)
                .countryId(7L)
                .build();

            when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(existingWarehouse));
            when(warehouseRepository.existsByNameWithDifferentId("Duplicate Name", warehouseId)).thenReturn(true);

            DuplicatedException thrown = assertThrows(DuplicatedException.class, () ->
                warehouseService.update(warehousePostVm, warehouseId)
            );

            assertThat(thrown.getMessage()).contains("already");
        }

        @Test
        void update_shouldUpdateAddressAndWarehouse() {
            Long warehouseId = 1L;

            Warehouse existingWarehouse = Warehouse.builder()
                .id(warehouseId)
                .name("Old Name")
                .addressId(100L)
                .build();

            WarehousePostVm warehousePostVm = WarehousePostVm.builder()
                .name("Updated Name")
                .contactName("Contact")
                .phone("111-1111")
                .addressLine1("New St")
                .addressLine2("Floor 5")
                .city("Updated City")
                .zipCode("11111")
                .districtId(10L)
                .stateOrProvinceId(11L)
                .countryId(12L)
                .build();

            when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(existingWarehouse));
            when(warehouseRepository.existsByNameWithDifferentId("Updated Name", warehouseId)).thenReturn(false);
            doNothing().when(locationService).updateAddress(anyLong(), any(AddressPostVm.class));
            when(warehouseRepository.save(any(Warehouse.class))).thenAnswer(inv -> inv.getArguments()[0]);

            warehouseService.update(warehousePostVm, warehouseId);

            assertEquals("Updated Name", existingWarehouse.getName());
            verify(locationService, times(1)).updateAddress(eq(100L), any(AddressPostVm.class));
        }
    }

    // ============= delete tests =============

    @Nested
    class DeleteTests {

        @Test
        void delete_shouldDeleteWarehouseAndAddress_whenWarehouseExists() {
            Long warehouseId = 1L;

            Warehouse warehouse = Warehouse.builder()
                .id(warehouseId)
                .name("Warehouse to Delete")
                .addressId(500L)
                .build();

            when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
            doNothing().when(warehouseRepository).deleteById(warehouseId);
            doNothing().when(locationService).deleteAddress(500L);

            warehouseService.delete(warehouseId);

            verify(warehouseRepository, times(1)).deleteById(warehouseId);
            verify(locationService, times(1)).deleteAddress(500L);
        }

        @Test
        void delete_shouldThrowNotFoundException_whenWarehouseNotFound() {
            Long warehouseId = 999L;

            when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

            NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                warehouseService.delete(warehouseId)
            );

            assertThat(thrown.getMessage()).contains("not found");
        }
    }

    // ============= getPageableWarehouses tests =============

    @Nested
    class GetPageableWarehousesTests {

        @Test
        void getPageableWarehouses_shouldReturnPagedResult() {
            int pageNo = 0;
            int pageSize = 10;

            Warehouse warehouse1 = Warehouse.builder().id(1L).name("Warehouse A").addressId(100L).build();
            Warehouse warehouse2 = Warehouse.builder().id(2L).name("Warehouse B").addressId(200L).build();

            org.springframework.data.domain.Page<Warehouse> page = new org.springframework.data.domain.PageImpl<>(
                List.of(warehouse1, warehouse2),
                org.springframework.data.domain.PageRequest.of(pageNo, pageSize),
                25
            );

            when(warehouseRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

            WarehouseListGetVm result = warehouseService.getPageableWarehouses(pageNo, pageSize);

            assertEquals(2, result.warehouseContent().size());
            assertEquals(0, result.pageNo());
            assertEquals(10, result.pageSize());
            assertEquals(25, result.totalElements());
            assertEquals(3, result.totalPages());
            assertEquals(false, result.isLast());
        }

        @Test
        void getPageableWarehouses_shouldMapWarehouseGetVmCorrectly() {
            int pageNo = 1;
            int pageSize = 5;

            Warehouse warehouse1 = Warehouse.builder().id(3L).name("Warehouse C").addressId(300L).build();

            org.springframework.data.domain.Page<Warehouse> page = new org.springframework.data.domain.PageImpl<>(
                List.of(warehouse1),
                org.springframework.data.domain.PageRequest.of(pageNo, pageSize),
                1
            );

            when(warehouseRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

            WarehouseListGetVm result = warehouseService.getPageableWarehouses(pageNo, pageSize);

            assertEquals(1, result.warehouseContent().size());
            assertEquals(3L, result.warehouseContent().get(0).id());
            assertEquals("Warehouse C", result.warehouseContent().get(0).name());
        }

        @Test
        void getPageableWarehouses_shouldHandleEmptyPage() {
            org.springframework.data.domain.Page<Warehouse> emptyPage = new org.springframework.data.domain.PageImpl<>(
                Collections.emptyList(),
                org.springframework.data.domain.PageRequest.of(0, 10),
                0
            );

            when(warehouseRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(emptyPage);

            WarehouseListGetVm result = warehouseService.getPageableWarehouses(0, 10);

            assertEquals(0, result.warehouseContent().size());
            assertEquals(0, result.totalElements());
            assertEquals(0, result.totalPages());
            assertEquals(true, result.isLast());
        }

        @Test
        void getPageableWarehouses_shouldSetIsLastTrue_whenOnLastPage() {
            org.springframework.data.domain.Page<Warehouse> lastPage = new org.springframework.data.domain.PageImpl<>(
                List.of(Warehouse.builder().id(3L).name("Last Warehouse").addressId(300L).build()),
                org.springframework.data.domain.PageRequest.of(2, 2),
                5
            );

            when(warehouseRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(lastPage);

            WarehouseListGetVm result = warehouseService.getPageableWarehouses(2, 2);

            assertEquals(3, result.totalPages());
            assertEquals(true, result.isLast());
        }
    }
}
