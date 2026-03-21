package com.yas.order.viewmodel.order;

import static org.junit.jupiter.api.Assertions.*;

import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.OrderItem;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrderVmTest {

    @Nested
    class FromModelTests {

        @Test
        void fromModel_shouldCreateOrderVmWithAllFields() {
            OrderAddress billingAddress = OrderAddress.builder()
                    .id(1L)
                    .phone("123456789")
                    .contactName("John Doe")
                    .addressLine1("123 Main St")
                    .city("Hanoi")
                    .zipCode("10000")
                    .countryName("Vietnam")
                    .build();

            OrderAddress shippingAddress = OrderAddress.builder()
                    .id(2L)
                    .phone("987654321")
                    .contactName("Jane Doe")
                    .addressLine1("456 Side St")
                    .city("HCM")
                    .zipCode("70000")
                    .countryName("Vietnam")
                    .build();

            OrderItem orderItem = OrderItem.builder()
                    .id(1L)
                    .productId(1L)
                    .productName("Test Product")
                    .quantity(2)
                    .productPrice(BigDecimal.valueOf(50))
                    .orderId(1L)
                    .note("Test note")
                    .discountAmount(BigDecimal.valueOf(5))
                    .taxAmount(BigDecimal.valueOf(10))
                    .taxPercent(BigDecimal.valueOf(10))
                    .build();

            Set<OrderItem> orderItems = new HashSet<>();
            orderItems.add(orderItem);

            Order order = Order.builder()
                    .id(1L)
                    .email("test@example.com")
                    .note("Order note")
                    .tax(10.0f)
                    .discount(5.0f)
                    .numberItem(2)
                    .totalPrice(BigDecimal.valueOf(100))
                    .deliveryFee(BigDecimal.valueOf(10))
                    .couponCode("DISCOUNT10")
                    .orderStatus(OrderStatus.COMPLETED)
                    .deliveryMethod(DeliveryMethod.VIETTEL_POST)
                    .deliveryStatus(DeliveryStatus.PREPARING)
                    .paymentStatus(PaymentStatus.COMPLETED)
                    .billingAddressId(billingAddress)
                    .shippingAddressId(shippingAddress)
                    .checkoutId("checkout-123")
                    .build();

            OrderVm result = OrderVm.fromModel(order, orderItems);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("test@example.com", result.email());
            assertEquals("Order note", result.note());
            assertEquals(10.0f, result.tax());
            assertEquals(5.0f, result.discount());
            assertEquals(2, result.numberItem());
            assertEquals(BigDecimal.valueOf(100), result.totalPrice());
            assertEquals(BigDecimal.valueOf(10), result.deliveryFee());
            assertEquals("DISCOUNT10", result.couponCode());
            assertEquals(OrderStatus.COMPLETED, result.orderStatus());
            assertEquals(DeliveryMethod.VIETTEL_POST, result.deliveryMethod());
            assertEquals(DeliveryStatus.PREPARING, result.deliveryStatus());
            assertEquals(PaymentStatus.COMPLETED, result.paymentStatus());
            assertEquals("checkout-123", result.checkoutId());
            assertNotNull(result.orderItemVms());
            assertEquals(1, result.orderItemVms().size());
            assertNotNull(result.shippingAddressVm());
            assertNotNull(result.billingAddressVm());
        }

        @Test
        void fromModel_shouldHandleNullOrderItems() {
            OrderAddress addr = OrderAddress.builder()
                    .id(1L)
                    .phone("123456789")
                    .contactName("John Doe")
                    .addressLine1("123 Main St")
                    .city("Hanoi")
                    .zipCode("10000")
                    .countryName("Vietnam")
                    .build();

            Order order = Order.builder()
                    .id(1L)
                    .email("test@example.com")
                    .orderStatus(OrderStatus.PENDING)
                    .paymentStatus(PaymentStatus.PENDING)
                    .totalPrice(BigDecimal.valueOf(100))
                    .billingAddressId(addr)
                    .shippingAddressId(addr)
                    .build();

            OrderVm result = OrderVm.fromModel(order, null);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertNull(result.orderItemVms());
            assertNotNull(result.billingAddressVm());
        }

        @Test
        void fromModel_shouldHandleEmptyOrderItems() {
            OrderAddress addr = OrderAddress.builder()
                    .id(1L)
                    .phone("123456789")
                    .contactName("John Doe")
                    .addressLine1("123 Main St")
                    .city("Hanoi")
                    .zipCode("10000")
                    .countryName("Vietnam")
                    .build();

            Order order = Order.builder()
                    .id(1L)
                    .email("test@example.com")
                    .orderStatus(OrderStatus.PENDING)
                    .paymentStatus(PaymentStatus.PENDING)
                    .totalPrice(BigDecimal.valueOf(100))
                    .billingAddressId(addr)
                    .shippingAddressId(addr)
                    .build();

            OrderVm result = OrderVm.fromModel(order, Set.of());

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertNotNull(result.orderItemVms());
            assertEquals(0, result.orderItemVms().size());
        }

        @Test
        void fromModel_shouldMapWithAddressesCorrectly() {
            OrderAddress addr = OrderAddress.builder()
                    .id(1L)
                    .phone("123456789")
                    .contactName("John Doe")
                    .addressLine1("123 Main St")
                    .city("Hanoi")
                    .zipCode("10000")
                    .countryName("Vietnam")
                    .build();

            Order order = Order.builder()
                    .id(1L)
                    .email("test@example.com")
                    .orderStatus(OrderStatus.PENDING)
                    .paymentStatus(PaymentStatus.PENDING)
                    .totalPrice(BigDecimal.valueOf(100))
                    .billingAddressId(addr)
                    .shippingAddressId(addr)
                    .build();

            OrderVm result = OrderVm.fromModel(order, null);

            assertNotNull(result);
            assertNotNull(result.billingAddressVm());
            assertNotNull(result.shippingAddressVm());
            assertEquals("123456789", result.billingAddressVm().phone());
        }

        @Test
        void fromModel_shouldMapOrderItemFieldsCorrectly() {
            OrderAddress addr = OrderAddress.builder()
                    .id(1L)
                    .phone("123456789")
                    .contactName("John Doe")
                    .addressLine1("123 Main St")
                    .city("Hanoi")
                    .zipCode("10000")
                    .countryName("Vietnam")
                    .build();

            OrderItem orderItem = OrderItem.builder()
                    .id(1L)
                    .productId(100L)
                    .productName("Special Product")
                    .quantity(5)
                    .productPrice(BigDecimal.valueOf(25.50))
                    .note("Fragile")
                    .discountAmount(BigDecimal.valueOf(2))
                    .taxAmount(BigDecimal.valueOf(3))
                    .taxPercent(BigDecimal.valueOf(10))
                    .orderId(10L)
                    .build();

            Order order = Order.builder()
                    .id(1L)
                    .email("test@example.com")
                    .orderStatus(OrderStatus.PENDING)
                    .paymentStatus(PaymentStatus.PENDING)
                    .totalPrice(BigDecimal.valueOf(100))
                    .billingAddressId(addr)
                    .shippingAddressId(addr)
                    .build();

            Set<OrderItem> orderItems = new HashSet<>();
            orderItems.add(orderItem);

            OrderVm result = OrderVm.fromModel(order, orderItems);

            assertNotNull(result.orderItemVms());
            OrderItemVm itemVm = result.orderItemVms().iterator().next();
            assertEquals(1L, itemVm.id());
            assertEquals(100L, itemVm.productId());
            assertEquals("Special Product", itemVm.productName());
            assertEquals(5, itemVm.quantity());
            assertEquals(BigDecimal.valueOf(25.50), itemVm.productPrice());
            assertEquals("Fragile", itemVm.note());
            assertEquals(BigDecimal.valueOf(2), itemVm.discountAmount());
            assertEquals(BigDecimal.valueOf(3), itemVm.taxAmount());
            assertEquals(BigDecimal.valueOf(10), itemVm.taxPercent());
            assertEquals(10L, itemVm.orderId());
        }
    }
}
