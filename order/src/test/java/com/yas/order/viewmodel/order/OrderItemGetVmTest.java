package com.yas.order.viewmodel.order;

import static org.junit.jupiter.api.Assertions.*;

import com.yas.order.model.OrderItem;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrderItemGetVmTest {

    @Nested
    class FromModelTests {

        @Test
        void fromModel_shouldCreateOrderItemGetVmWithAllFields() {
            OrderItem orderItem = OrderItem.builder()
                    .id(1L)
                    .productId(100L)
                    .productName("Test Product")
                    .quantity(5)
                    .productPrice(BigDecimal.valueOf(30))
                    .discountAmount(BigDecimal.valueOf(5))
                    .taxAmount(BigDecimal.valueOf(10))
                    .build();

            OrderItemGetVm result = OrderItemGetVm.fromModel(orderItem);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals(100L, result.productId());
            assertEquals("Test Product", result.productName());
            assertEquals(5, result.quantity());
            assertEquals(BigDecimal.valueOf(30), result.productPrice());
            assertEquals(BigDecimal.valueOf(5), result.discountAmount());
            assertEquals(BigDecimal.valueOf(10), result.taxAmount());
        }

        @Test
        void fromModel_shouldHandleNullDiscountAmount() {
            OrderItem orderItem = OrderItem.builder()
                    .id(1L)
                    .productId(1L)
                    .productName("Product")
                    .quantity(1)
                    .productPrice(BigDecimal.valueOf(10))
                    .discountAmount(null)
                    .taxAmount(null)
                    .build();

            OrderItemGetVm result = OrderItemGetVm.fromModel(orderItem);

            assertNotNull(result);
            assertNull(result.discountAmount());
            assertNull(result.taxAmount());
        }
    }

    @Nested
    class FromModelsTests {

        @Test
        void fromModels_shouldConvertCollectionToList() {
            OrderItem item1 = OrderItem.builder()
                    .id(1L)
                    .productId(1L)
                    .productName("Product 1")
                    .quantity(1)
                    .productPrice(BigDecimal.valueOf(10))
                    .build();

            OrderItem item2 = OrderItem.builder()
                    .id(2L)
                    .productId(2L)
                    .productName("Product 2")
                    .quantity(2)
                    .productPrice(BigDecimal.valueOf(20))
                    .build();

            List<OrderItem> orderItems = List.of(item1, item2);

            List<OrderItemGetVm> result = OrderItemGetVm.fromModels(orderItems);

            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        void fromModels_shouldReturnEmptyListWhenNull() {
            List<OrderItemGetVm> result = OrderItemGetVm.fromModels(null);

            assertNotNull(result);
            assertEquals(0, result.size());
        }

        @Test
        void fromModels_shouldReturnEmptyListWhenEmpty() {
            List<OrderItemGetVm> result = OrderItemGetVm.fromModels(Set.of());

            assertNotNull(result);
            assertEquals(0, result.size());
        }

        @Test
        void fromModels_shouldHandleSingleItem() {
            OrderItem item = OrderItem.builder()
                    .id(1L)
                    .productId(1L)
                    .productName("Single Product")
                    .quantity(1)
                    .productPrice(BigDecimal.valueOf(50))
                    .build();

            List<OrderItemGetVm> result = OrderItemGetVm.fromModels(List.of(item));

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Single Product", result.get(0).productName());
        }
    }
}
