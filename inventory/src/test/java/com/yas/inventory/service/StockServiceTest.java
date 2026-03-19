package com.yas.inventory.service;

import static com.yas.inventory.util.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.commonlibrary.exception.StockExistingException;
import com.yas.inventory.model.Stock;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.product.ProductInfoVm;
import com.yas.inventory.viewmodel.product.ProductQuantityPostVm;
import com.yas.inventory.viewmodel.stock.StockPostVm;
import com.yas.inventory.viewmodel.stock.StockQuantityUpdateVm;
import com.yas.inventory.viewmodel.stock.StockQuantityVm;
import com.yas.inventory.viewmodel.stock.StockVm;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class StockServiceTest {

    private WarehouseRepository warehouseRepository;
    private StockRepository stockRepository;
    private ProductService productService;
    private WarehouseService warehouseService;
    private StockHistoryService stockHistoryService;
    private StockService stockService;

    @BeforeEach
    void setUp() {
        warehouseRepository = mock(WarehouseRepository.class);
        stockRepository = mock(StockRepository.class);
        productService = mock(ProductService.class);
        warehouseService = mock(WarehouseService.class);
        stockHistoryService = mock(StockHistoryService.class);
        stockService = new StockService(
            warehouseRepository,
            stockRepository,
            productService,
            warehouseService,
            stockHistoryService
        );
        setUpSecurityContext("test-user");
    }

    // ============= addProductIntoWarehouse tests =============

    @Nested
    class AddProductIntoWarehouseTests {

        @Test
        void addProductIntoWarehouse_shouldSaveStock_whenAllConditionsPass() {
            Long warehouseId = 1L;
            Long productId = 10L;

            Warehouse warehouse = new Warehouse();
            warehouse.setId(warehouseId);
            warehouse.setName("Warehouse A");

            ProductInfoVm productInfoVm = new ProductInfoVm(productId, "Product X", "SKU-X", true);
            StockPostVm stockPostVm = new StockPostVm(productId, warehouseId);

            when(stockRepository.existsByWarehouseIdAndProductId(warehouseId, productId)).thenReturn(false);
            when(productService.getProduct(productId)).thenReturn(productInfoVm);
            when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
            when(stockRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArguments()[0]);

            stockService.addProductIntoWarehouse(List.of(stockPostVm));

            verify(stockRepository, times(1)).saveAll(anyList());
        }

        @Test
        void addProductIntoWarehouse_shouldThrowStockExistingException_whenStockAlreadyExists() {
            Long warehouseId = 1L;
            Long productId = 10L;
            StockPostVm stockPostVm = new StockPostVm(productId, warehouseId);

            when(stockRepository.existsByWarehouseIdAndProductId(warehouseId, productId)).thenReturn(true);

            StockExistingException thrown = assertThrows(StockExistingException.class, () ->
                stockService.addProductIntoWarehouse(List.of(stockPostVm))
            );

            assertThat(thrown.getMessage()).contains("already existing warehouse");
        }

        @Test
        void addProductIntoWarehouse_shouldThrowNotFoundException_whenProductNotFound() {
            Long warehouseId = 1L;
            Long productId = 999L;
            StockPostVm stockPostVm = new StockPostVm(productId, warehouseId);

            when(stockRepository.existsByWarehouseIdAndProductId(warehouseId, productId)).thenReturn(false);
            when(productService.getProduct(productId)).thenReturn(null);

            NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                stockService.addProductIntoWarehouse(List.of(stockPostVm))
            );

            assertThat(thrown.getMessage()).contains("not found");
        }

        @Test
        void addProductIntoWarehouse_shouldThrowNotFoundException_whenWarehouseNotFound() {
            Long warehouseId = 999L;
            Long productId = 10L;
            StockPostVm stockPostVm = new StockPostVm(productId, warehouseId);

            when(stockRepository.existsByWarehouseIdAndProductId(warehouseId, productId)).thenReturn(false);
            when(productService.getProduct(productId)).thenReturn(new ProductInfoVm(productId, "Product X", "SKU-X", true));
            when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.empty());

            NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                stockService.addProductIntoWarehouse(List.of(stockPostVm))
            );

            assertThat(thrown.getMessage()).contains("warehouse");
        }

        @Test
        void addProductIntoWarehouse_shouldSetQuantityAndReservedQuantityToZero() {
            Long warehouseId = 1L;
            Long productId = 10L;

            Warehouse warehouse = new Warehouse();
            warehouse.setId(warehouseId);

            StockPostVm stockPostVm = new StockPostVm(productId, warehouseId);

            when(stockRepository.existsByWarehouseIdAndProductId(warehouseId, productId)).thenReturn(false);
            when(productService.getProduct(productId)).thenReturn(new ProductInfoVm(productId, "Product X", "SKU-X", true));
            when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<Stock>> stockCaptor = ArgumentCaptor.forClass(List.class);
            when(stockRepository.saveAll(stockCaptor.capture())).thenAnswer(invocation -> invocation.getArguments()[0]);

            stockService.addProductIntoWarehouse(List.of(stockPostVm));

            List<Stock> savedStocks = stockCaptor.getValue();
            assertEquals(1, savedStocks.size());
            assertEquals(0L, savedStocks.getFirst().getQuantity());
            assertEquals(0L, savedStocks.getFirst().getReservedQuantity());
        }

        @Test
        void addProductIntoWarehouse_shouldProcessMultipleProducts() {
            Long warehouseId = 1L;
            Long productId1 = 10L;
            Long productId2 = 20L;

            Warehouse warehouse = new Warehouse();
            warehouse.setId(warehouseId);

            StockPostVm stockPostVm1 = new StockPostVm(productId1, warehouseId);
            StockPostVm stockPostVm2 = new StockPostVm(productId2, warehouseId);

            when(stockRepository.existsByWarehouseIdAndProductId(anyLong(), anyLong())).thenReturn(false);
            when(productService.getProduct(anyLong()))
                .thenReturn(new ProductInfoVm(productId1, "Product A", "SKU-A", true))
                .thenReturn(new ProductInfoVm(productId2, "Product B", "SKU-B", true));
            when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
            when(stockRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArguments()[0]);

            stockService.addProductIntoWarehouse(List.of(stockPostVm1, stockPostVm2));

            verify(stockRepository, times(1)).saveAll(anyList());
        }
    }

    // ============= getStocksByWarehouseIdAndProductNameAndSku tests =============

    @Nested
    class GetStocksByWarehouseIdAndProductNameAndSkuTests {

        @Test
        void getStocks_shouldReturnStockVms_whenProductsExistInWarehouse() {
            Long warehouseId = 1L;
            String productName = "Product";
            String productSku = "SKU";

            Warehouse warehouse = new Warehouse();
            warehouse.setId(warehouseId);

            Stock stock = Stock.builder()
                .id(1L)
                .productId(10L)
                .quantity(50L)
                .reservedQuantity(5L)
                .warehouse(warehouse)
                .build();

            ProductInfoVm productInfoVm = new ProductInfoVm(10L, productName, productSku, true);

            when(warehouseService.getProductWarehouse(anyLong(), anyString(), anyString(), any()))
                .thenReturn(List.of(productInfoVm));
            when(stockRepository.findByWarehouseIdAndProductIdIn(anyLong(), anyList()))
                .thenReturn(List.of(stock));

            List<StockVm> result = stockService.getStocksByWarehouseIdAndProductNameAndSku(
                warehouseId, productName, productSku);

            assertEquals(1, result.size());
            assertEquals(10L, result.getFirst().productId());
            assertEquals(productName, result.getFirst().productName());
            assertEquals(productSku, result.getFirst().productSku());
            assertEquals(50L, result.getFirst().quantity());
        }

        @Test
        void getStocks_shouldReturnEmptyList_whenNoProductsMatch() {
            Long warehouseId = 1L;
            String productName = "NonExistent";
            String productSku = "NOTFOUND";

            when(warehouseService.getProductWarehouse(anyLong(), anyString(), anyString(), any()))
                .thenReturn(Collections.emptyList());

            List<StockVm> result = stockService.getStocksByWarehouseIdAndProductNameAndSku(
                warehouseId, productName, productSku);

            assertEquals(0, result.size());
        }

        @Test
        void getStocks_shouldMapProductInfoCorrectly() {
            Long warehouseId = 1L;

            Warehouse warehouse = new Warehouse();
            warehouse.setId(warehouseId);

            Stock stock1 = Stock.builder().id(1L).productId(10L).quantity(100L).reservedQuantity(10L).warehouse(warehouse).build();
            Stock stock2 = Stock.builder().id(2L).productId(20L).quantity(200L).reservedQuantity(20L).warehouse(warehouse).build();

            ProductInfoVm productInfoVm1 = new ProductInfoVm(10L, "Product One", "SKU-ONE", true);
            ProductInfoVm productInfoVm2 = new ProductInfoVm(20L, "Product Two", "SKU-TWO", true);

            when(warehouseService.getProductWarehouse(anyLong(), any(), any(), any()))
                .thenReturn(List.of(productInfoVm1, productInfoVm2));
            when(stockRepository.findByWarehouseIdAndProductIdIn(anyLong(), anyList()))
                .thenReturn(List.of(stock1, stock2));

            List<StockVm> result = stockService.getStocksByWarehouseIdAndProductNameAndSku(
                warehouseId, null, null);

            assertEquals(2, result.size());
        }
    }

    // ============= updateProductQuantityInStock tests =============

    @Nested
    class UpdateProductQuantityInStockTests {

        @Test
        void updateProductQuantity_shouldIncreaseQuantity_whenPositiveAdjustedQuantity() {
            Long stockId = 1L;
            Long initialQuantity = 10L;
            Long adjustedQuantity = 5L;

            Stock stock = Stock.builder()
                .id(stockId)
                .productId(10L)
                .quantity(initialQuantity)
                .reservedQuantity(0L)
                .build();

            StockQuantityVm sqvm = new StockQuantityVm(stockId, adjustedQuantity, "Restocking");
            StockQuantityUpdateVm requestBody = new StockQuantityUpdateVm(List.of(sqvm));

            when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock));
            when(stockRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArguments()[0]);
            doNothing().when(stockHistoryService).createStockHistories(anyList(), anyList());
            doNothing().when(productService).updateProductQuantity(anyList());

            stockService.updateProductQuantityInStock(requestBody);

            assertEquals(initialQuantity + adjustedQuantity, stock.getQuantity());
            verify(stockHistoryService, times(1)).createStockHistories(anyList(), anyList());
        }

        @Test
        void updateProductQuantity_shouldDecreaseQuantity_whenNegativeAdjustedQuantity() {
            Long stockId = 1L;
            Long initialQuantity = 100L;
            Long adjustedQuantity = -30L;

            Stock stock = Stock.builder()
                .id(stockId)
                .productId(10L)
                .quantity(initialQuantity)
                .reservedQuantity(0L)
                .build();

            StockQuantityVm sqvm = new StockQuantityVm(stockId, adjustedQuantity, "Sale");
            StockQuantityUpdateVm requestBody = new StockQuantityUpdateVm(List.of(sqvm));

            when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock));
            when(stockRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArguments()[0]);
            doNothing().when(stockHistoryService).createStockHistories(anyList(), anyList());
            doNothing().when(productService).updateProductQuantity(anyList());

            stockService.updateProductQuantityInStock(requestBody);

            assertEquals(initialQuantity + adjustedQuantity, stock.getQuantity());
        }

        @Test
        void updateProductQuantity_shouldThrowBadRequestException_whenAdjustmentWouldResultInNegativeQuantity() {
            Long stockId = 1L;
            Stock stock = Stock.builder()
                .id(stockId)
                .productId(10L)
                .quantity(-15L)
                .reservedQuantity(0L)
                .build();

            StockQuantityVm sqvm = new StockQuantityVm(stockId, -10L, "Oversold");
            StockQuantityUpdateVm requestBody = new StockQuantityUpdateVm(List.of(sqvm));

            when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock));

            BadRequestException thrown = assertThrows(BadRequestException.class, () ->
                stockService.updateProductQuantityInStock(requestBody)
            );

            assertThat(thrown.getMessage()).contains("Invalid adjusted quantity make a negative quantity");
        }

        @Test
        void updateProductQuantity_shouldHandleNullQuantity_asZero() {
            Long stockId = 1L;
            Stock stock = Stock.builder()
                .id(stockId)
                .productId(10L)
                .quantity(50L)
                .reservedQuantity(0L)
                .build();

            StockQuantityVm sqvm = new StockQuantityVm(stockId, null, "Note");
            StockQuantityUpdateVm requestBody = new StockQuantityUpdateVm(List.of(sqvm));

            when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock));
            when(stockRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArguments()[0]);
            doNothing().when(stockHistoryService).createStockHistories(anyList(), anyList());
            doNothing().when(productService).updateProductQuantity(anyList());

            stockService.updateProductQuantityInStock(requestBody);

            assertEquals(50L, stock.getQuantity());
        }

        @Test
        void updateProductQuantity_shouldUpdateProductQuantityService_whenProductQuantityListNotEmpty() {
            Long stockId = 1L;
            Stock stock = Stock.builder()
                .id(stockId)
                .productId(10L)
                .quantity(50L)
                .reservedQuantity(0L)
                .build();

            StockQuantityVm sqvm = new StockQuantityVm(stockId, 10L, "Restock");
            StockQuantityUpdateVm requestBody = new StockQuantityUpdateVm(List.of(sqvm));

            when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock));
            when(stockRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArguments()[0]);
            doNothing().when(stockHistoryService).createStockHistories(anyList(), anyList());
            doNothing().when(productService).updateProductQuantity(anyList());

            stockService.updateProductQuantityInStock(requestBody);

            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<ProductQuantityPostVm>> captor = ArgumentCaptor.forClass(List.class);
            verify(productService, times(1)).updateProductQuantity(captor.capture());

            List<ProductQuantityPostVm> captured = captor.getValue();
            assertEquals(1, captured.size());
        }

        @Test
        void updateProductQuantity_shouldSkipStockNotInQuantityList() {
            Long stockId1 = 1L;
            Long stockId2 = 2L;

            Stock stock1 = Stock.builder().id(stockId1).productId(10L).quantity(50L).reservedQuantity(0L).build();
            Stock stock2 = Stock.builder().id(stockId2).productId(20L).quantity(100L).reservedQuantity(0L).build();

            StockQuantityVm sqvm = new StockQuantityVm(stockId1, 10L, "Restock");
            StockQuantityUpdateVm requestBody = new StockQuantityUpdateVm(List.of(sqvm));

            when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock1, stock2));
            when(stockRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArguments()[0]);
            doNothing().when(stockHistoryService).createStockHistories(anyList(), anyList());
            doNothing().when(productService).updateProductQuantity(anyList());

            stockService.updateProductQuantityInStock(requestBody);

            assertEquals(60L, stock1.getQuantity());
            assertEquals(100L, stock2.getQuantity());
        }

        @Test
        void updateProductQuantity_shouldAllowZeroAdjustedQuantity() {
            Long stockId = 1L;
            Stock stock = Stock.builder()
                .id(stockId)
                .productId(10L)
                .quantity(50L)
                .reservedQuantity(0L)
                .build();

            StockQuantityVm sqvm = new StockQuantityVm(stockId, 0L, "No change");
            StockQuantityUpdateVm requestBody = new StockQuantityUpdateVm(List.of(sqvm));

            when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock));
            when(stockRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArguments()[0]);
            doNothing().when(stockHistoryService).createStockHistories(anyList(), anyList());
            doNothing().when(productService).updateProductQuantity(anyList());

            stockService.updateProductQuantityInStock(requestBody);

            assertEquals(50L, stock.getQuantity());
        }

        @Test
        void updateProductQuantity_shouldAllowAdjustmentEqualToCurrentQuantity() {
            Long stockId = 1L;
            Stock stock = Stock.builder()
                .id(stockId)
                .productId(10L)
                .quantity(50L)
                .reservedQuantity(0L)
                .build();

            StockQuantityVm sqvm = new StockQuantityVm(stockId, -50L, "Clear stock");
            StockQuantityUpdateVm requestBody = new StockQuantityUpdateVm(List.of(sqvm));

            when(stockRepository.findAllById(anyList())).thenReturn(List.of(stock));
            when(stockRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArguments()[0]);
            doNothing().when(stockHistoryService).createStockHistories(anyList(), anyList());
            doNothing().when(productService).updateProductQuantity(anyList());

            stockService.updateProductQuantityInStock(requestBody);

            assertEquals(0L, stock.getQuantity());
        }
    }
}
