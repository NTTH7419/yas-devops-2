package com.yas.order.viewmodel.order;

import static org.junit.jupiter.api.Assertions.*;

import com.yas.order.model.Order;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrderGetVmTest {

    @Nested
    class FromModelTests {

        @Test
        void fromModel_shouldCreateOrderGetVmWithAllFields() {
            Order order = Order.builder()
                    .id(1L)
                    .orderStatus(OrderStatus.PENDING)
                    .totalPrice(BigDecimal.valueOf(200))
                    .deliveryStatus(DeliveryStatus.DELIVERING)
                    .deliveryMethod(DeliveryMethod.YAS_EXPRESS)
                    .build();
            order.setCreatedOn(ZonedDateTime.now());

            OrderItem orderItem = OrderItem.builder()
                    .id(1L)
                    .productId(1L)
                    .productName("Product A")
                    .quantity(3)
                    .productPrice(BigDecimal.valueOf(50))
                    .discountAmount(BigDecimal.valueOf(5))
                    .taxAmount(BigDecimal.valueOf(10))
                    .build();

            Set<OrderItem> orderItems = new HashSet<>();
            orderItems.add(orderItem);

            OrderGetVm result = OrderGetVm.fromModel(order, orderItems);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals(OrderStatus.PENDING, result.orderStatus());
            assertEquals(BigDecimal.valueOf(200), result.totalPrice());
            assertEquals(DeliveryStatus.DELIVERING, result.deliveryStatus());
            assertEquals(DeliveryMethod.YAS_EXPRESS, result.deliveryMethod());
            assertNotNull(result.orderItems());
            assertEquals(1, result.orderItems().size());
            assertNotNull(result.createdOn());
        }

        @Test
        void fromModel_shouldHandleNullOrderItems() {
            Order order = Order.builder()
                    .id(1L)
                    .orderStatus(OrderStatus.PENDING)
                    .totalPrice(BigDecimal.valueOf(100))
                    .deliveryStatus(null)
                    .deliveryMethod(null)
                    .build();
            order.setCreatedOn(ZonedDateTime.now());

            OrderGetVm result = OrderGetVm.fromModel(order, null);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertNotNull(result.orderItems());
            assertEquals(0, result.orderItems().size());
        }

        @Test
        void fromModel_shouldHandleEmptyOrderItems() {
            Order order = Order.builder()
                    .id(1L)
                    .orderStatus(OrderStatus.PENDING)
                    .totalPrice(BigDecimal.valueOf(100))
                    .build();
            order.setCreatedOn(ZonedDateTime.now());

            OrderGetVm result = OrderGetVm.fromModel(order, Set.of());

            assertNotNull(result);
            assertNotNull(result.orderItems());
            assertEquals(0, result.orderItems().size());
        }

        @Test
        void fromModel_shouldMapOrderItemFieldsCorrectly() {
            Order order = Order.builder()
                    .id(1L)
                    .orderStatus(OrderStatus.PENDING)
                    .totalPrice(BigDecimal.valueOf(100))
                    .build();
            order.setCreatedOn(ZonedDateTime.now());

            OrderItem orderItem = OrderItem.builder()
                    .id(10L)
                    .productId(100L)
                    .productName("Amazing Product")
                    .quantity(5)
                    .productPrice(BigDecimal.valueOf(20))
                    .discountAmount(BigDecimal.valueOf(10))
                    .taxAmount(BigDecimal.valueOf(15))
                    .build();

            Set<OrderItem> orderItems = new HashSet<>();
            orderItems.add(orderItem);

            OrderGetVm result = OrderGetVm.fromModel(order, orderItems);

            OrderItemGetVm itemVm = result.orderItems().get(0);
            assertEquals(10L, itemVm.id());
            assertEquals(100L, itemVm.productId());
            assertEquals("Amazing Product", itemVm.productName());
            assertEquals(5, itemVm.quantity());
            assertEquals(BigDecimal.valueOf(20), itemVm.productPrice());
            assertEquals(BigDecimal.valueOf(10), itemVm.discountAmount());
            assertEquals(BigDecimal.valueOf(15), itemVm.taxAmount());
        }
    }
}
