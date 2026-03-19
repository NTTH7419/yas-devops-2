package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.inventory.model.Stock;
import com.yas.inventory.model.StockHistory;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockHistoryRepository;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import com.yas.inventory.viewmodel.stockhistory.StockHistoryListVm;
import com.yas.inventory.viewmodel.stockhistory.StockHistoryVm;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class StockHistoryServiceTest {

    private StockHistoryRepository stockHistoryRepository;
    private ProductService productService;
    private StockHistoryService stockHistoryService;

    @BeforeEach
    void setUp() {
        stockHistoryRepository = mock(StockHistoryRepository.class);
        productService = mock(ProductService.class);
        stockHistoryService = new StockHistoryService(stockHistoryRepository, productService);
    }

    // ============= createStockHistories tests =============

    @Nested
    class CreateStockHistoriesTests {

        @Test
        void createStockHistories_shouldSaveAllHistories_whenMatchingStocksExist() {
            Warehouse warehouse = new Warehouse();
            warehouse.setId(1L);

            Stock stock1 = Stock.builder().id(1L).productId(10L).quantity(100L).warehouse(warehouse).build();
            Stock stock2 = Stock.builder().id(2L).productId(20L).quantity(200L).warehouse(warehouse).build();

            StockQuantityVm sqvm1 = new StockQuantityVm(1L, 50L, "Restock 1");
            StockQuantityVm sqvm2 = new StockQuantityVm(2L, -30L, "Sold 1");
            List<StockQuantityVm> sqvmList = List.of(sqvm1, sqvm2);

            when(stockHistoryRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArguments()[0]);

            stockHistoryService.createStockHistories(List.of(stock1, stock2), sqvmList);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);
            verify(stockHistoryRepository, times(1)).saveAll(captor.capture());

            List<StockHistory> saved = captor.getValue();
            assertEquals(2, saved.size());
        }

        @Test
        void createStockHistories_shouldMapFieldsCorrectly() {
            Warehouse warehouse = new Warehouse();
            warehouse.setId(1L);

            Stock stock = Stock.builder()
                .id(1L)
                .productId(10L)
                .quantity(100L)
                .warehouse(warehouse)
                .build();

            StockQuantityVm sqvm = new StockQuantityVm(1L, 25L, "Restock note");
            List<StockQuantityVm> sqvmList = List.of(sqvm);

            when(stockHistoryRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArguments()[0]);

            stockHistoryService.createStockHistories(List.of(stock), sqvmList);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);
            verify(stockHistoryRepository, times(1)).saveAll(captor.capture());

            List<StockHistory> saved = captor.getValue();
            assertEquals(1, saved.size());
            assertEquals(10L, saved.getFirst().getProductId());
            assertEquals(25L, saved.getFirst().getAdjustedQuantity());
            assertEquals("Restock note", saved.getFirst().getNote());
            assertEquals(warehouse, saved.getFirst().getWarehouse());
        }

        @Test
        void createStockHistories_shouldSkipStockWithNoMatchingQuantityVm() {
            Warehouse warehouse = new Warehouse();
            warehouse.setId(1L);

            Stock stock1 = Stock.builder().id(1L).productId(10L).quantity(100L).warehouse(warehouse).build();
            Stock stock2 = Stock.builder().id(2L).productId(20L).quantity(200L).warehouse(warehouse).build();

            StockQuantityVm sqvm1 = new StockQuantityVm(1L, 50L, "Only stock 1");
            List<StockQuantityVm> sqvmList = List.of(sqvm1);

            when(stockHistoryRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArguments()[0]);

            stockHistoryService.createStockHistories(List.of(stock1, stock2), sqvmList);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);
            verify(stockHistoryRepository, times(1)).saveAll(captor.capture());

            List<StockHistory> saved = captor.getValue();
            assertEquals(1, saved.size());
            assertEquals(10L, saved.getFirst().getProductId());
        }

        @Test
        void createStockHistories_shouldHandleNullNote() {
            Warehouse warehouse = new Warehouse();
            warehouse.setId(1L);

            Stock stock = Stock.builder()
                .id(1L)
                .productId(10L)
                .quantity(100L)
                .warehouse(warehouse)
                .build();

            StockQuantityVm sqvm = new StockQuantityVm(1L, 50L, null);
            List<StockQuantityVm> sqvmList = List.of(sqvm);

            when(stockHistoryRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArguments()[0]);

            stockHistoryService.createStockHistories(List.of(stock), sqvmList);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);
            verify(stockHistoryRepository, times(1)).saveAll(captor.capture());

            List<StockHistory> saved = captor.getValue();
            assertEquals(1, saved.size());
            assertEquals(null, saved.getFirst().getNote());
        }

        @Test
        void createStockHistories_shouldSaveEmptyList_whenNoMatchingStocks() {
            Stock stock = Stock.builder()
                .id(99L)
                .productId(999L)
                .quantity(100L)
                .build();

            StockQuantityVm sqvm = new StockQuantityVm(1L, 50L, "No matching stock");
            List<StockQuantityVm> sqvmList = List.of(sqvm);

            when(stockHistoryRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArguments()[0]);

            stockHistoryService.createStockHistories(List.of(stock), sqvmList);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);
            verify(stockHistoryRepository, times(1)).saveAll(captor.capture());

            List<StockHistory> saved = captor.getValue();
            assertEquals(0, saved.size());
        }

        @Test
        void createStockHistories_shouldSaveEmptyList_whenEmptyStocksList() {
            StockQuantityVm sqvm = new StockQuantityVm(1L, 50L, "No stocks");
            List<StockQuantityVm> sqvmList = List.of(sqvm);

            when(stockHistoryRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArguments()[0]);

            stockHistoryService.createStockHistories(Collections.emptyList(), sqvmList);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<StockHistory>> captor = ArgumentCaptor.forClass(List.class);
            verify(stockHistoryRepository, times(1)).saveAll(captor.capture());

            List<StockHistory> saved = captor.getValue();
            assertEquals(0, saved.size());
        }
    }

    // ============= getStockHistories tests =============

    @Nested
    class GetStockHistoriesTests {

        @Test
        void getStockHistories_shouldReturnHistoryListVm_whenHistoriesExist() {
            Long productId = 10L;
            Long warehouseId = 1L;

            Warehouse warehouse = new Warehouse();
            warehouse.setId(warehouseId);

            StockHistory history1 = StockHistory.builder()
                .id(1L)
                .productId(productId)
                .adjustedQuantity(50L)
                .note("Restock")
                .warehouse(warehouse)
                .build();
            StockHistory history2 = StockHistory.builder()
                .id(2L)
                .productId(productId)
                .adjustedQuantity(-20L)
                .note("Sale")
                .warehouse(warehouse)
                .build();

            ProductInfoVm productInfoVm = new ProductInfoVm(productId, "Product A", "SKU-A", true);

            when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(productId, warehouseId))
                .thenReturn(List.of(history1, history2));
            when(productService.getProduct(productId)).thenReturn(productInfoVm);

            StockHistoryListVm result = stockHistoryService.getStockHistories(productId, warehouseId);

            assertThat(result.data()).hasSize(2);
            assertEquals("Product A", result.data().get(0).productName());
            assertEquals(50L, result.data().get(0).adjustedQuantity());
            assertEquals(-20L, result.data().get(1).adjustedQuantity());
        }

        @Test
        void getStockHistories_shouldReturnEmptyList_whenNoHistoriesExist() {
            Long productId = 10L;
            Long warehouseId = 1L;

            ProductInfoVm productInfoVm = new ProductInfoVm(productId, "Product A", "SKU-A", true);

            when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(productId, warehouseId))
                .thenReturn(Collections.emptyList());
            when(productService.getProduct(productId)).thenReturn(productInfoVm);

            StockHistoryListVm result = stockHistoryService.getStockHistories(productId, warehouseId);

            assertThat(result.data()).isEmpty();
        }

        @Test
        void getStockHistories_shouldMapCreatedByAndCreatedOnFields() {
            Long productId = 10L;
            Long warehouseId = 1L;

            Warehouse warehouse = new Warehouse();
            warehouse.setId(warehouseId);

            StockHistory history = StockHistory.builder()
                .id(1L)
                .productId(productId)
                .adjustedQuantity(100L)
                .note("Manual adjustment")
                .warehouse(warehouse)
                .build();

            ProductInfoVm productInfoVm = new ProductInfoVm(productId, "Product B", "SKU-B", true);

            when(stockHistoryRepository.findByProductIdAndWarehouseIdOrderByCreatedOnDesc(productId, warehouseId))
                .thenReturn(List.of(history));
            when(productService.getProduct(productId)).thenReturn(productInfoVm);

            StockHistoryVm vm = stockHistoryService.getStockHistories(productId, warehouseId).data().get(0);

            assertEquals("Product B", vm.productName());
            assertEquals(100L, vm.adjustedQuantity());
            assertEquals("Manual adjustment", vm.note());
        }
    }
}
