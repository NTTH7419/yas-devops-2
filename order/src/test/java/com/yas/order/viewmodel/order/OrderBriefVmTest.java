package com.yas.order.viewmodel.order;

import static org.junit.jupiter.api.Assertions.*;

import com.yas.order.model.Order;
import com.yas.order.model.OrderAddress;
import com.yas.order.model.enumeration.DeliveryMethod;
import com.yas.order.model.enumeration.DeliveryStatus;
import com.yas.order.model.enumeration.OrderStatus;
import com.yas.order.model.enumeration.PaymentStatus;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrderBriefVmTest {

    private OrderAddress createBillingAddress() {
        return OrderAddress.builder()
                .id(1L)
                .phone("123456789")
                .contactName("John Doe")
                .addressLine1("123 Main St")
                .city("Hanoi")
                .zipCode("10000")
                .countryName("Vietnam")
                .build();
    }

    @Nested
    class FromModelTests {

        @Test
        void fromModel_shouldCreateOrderBriefVmWithAllFields() {
            OrderAddress billingAddress = createBillingAddress();

            Order order = Order.builder()
                    .id(1L)
                    .email("test@example.com")
                    .billingAddressId(billingAddress)
                    .totalPrice(BigDecimal.valueOf(150.50))
                    .orderStatus(OrderStatus.COMPLETED)
                    .deliveryMethod(DeliveryMethod.GRAB_EXPRESS)
                    .deliveryStatus(DeliveryStatus.DELIVERING)
                    .paymentStatus(PaymentStatus.COMPLETED)
                    .build();
            order.setCreatedOn(ZonedDateTime.now());

            OrderBriefVm result = OrderBriefVm.fromModel(order);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("test@example.com", result.email());
            assertEquals(BigDecimal.valueOf(150.50), result.totalPrice());
            assertEquals(OrderStatus.COMPLETED, result.orderStatus());
            assertEquals(DeliveryMethod.GRAB_EXPRESS, result.deliveryMethod());
            assertEquals(DeliveryStatus.DELIVERING, result.deliveryStatus());
            assertEquals(PaymentStatus.COMPLETED, result.paymentStatus());
            assertNotNull(result.billingAddressVm());
            assertEquals("123456789", result.billingAddressVm().phone());
            assertNotNull(result.createdOn());
        }

        @Test
        void fromModel_shouldMapBillingAddressFieldsCorrectly() {
            OrderAddress billingAddress = OrderAddress.builder()
                    .id(1L)
                    .phone("999888777")
                    .contactName("Contact Person")
                    .addressLine1("456 Street")
                    .addressLine2("Floor 3")
                    .city("Da Nang")
                    .zipCode("55000")
                    .districtId(5L)
                    .districtName("Hai Chau")
                    .stateOrProvinceId(3L)
                    .stateOrProvinceName("Da Nang City")
                    .countryId(1L)
                    .countryName("Vietnam")
                    .build();

            Order order = Order.builder()
                    .id(1L)
                    .email("test@example.com")
                    .billingAddressId(billingAddress)
                    .totalPrice(BigDecimal.valueOf(100))
                    .orderStatus(OrderStatus.PENDING)
                    .paymentStatus(PaymentStatus.PENDING)
                    .build();

            OrderBriefVm result = OrderBriefVm.fromModel(order);

            assertNotNull(result.billingAddressVm());
            assertEquals(1L, result.billingAddressVm().id());
            assertEquals("999888777", result.billingAddressVm().phone());
            assertEquals("Contact Person", result.billingAddressVm().contactName());
            assertEquals("456 Street", result.billingAddressVm().addressLine1());
            assertEquals("Floor 3", result.billingAddressVm().addressLine2());
            assertEquals("Da Nang", result.billingAddressVm().city());
            assertEquals("55000", result.billingAddressVm().zipCode());
            assertEquals(5L, result.billingAddressVm().districtId());
            assertEquals("Hai Chau", result.billingAddressVm().districtName());
            assertEquals(3L, result.billingAddressVm().stateOrProvinceId());
            assertEquals("Da Nang City", result.billingAddressVm().stateOrProvinceName());
            assertEquals(1L, result.billingAddressVm().countryId());
            assertEquals("Vietnam", result.billingAddressVm().countryName());
        }

        @Test
        void fromModel_shouldHandleNullCreatedOn() {
            OrderAddress billingAddress = createBillingAddress();

            Order order = Order.builder()
                    .id(1L)
                    .email("test@example.com")
                    .billingAddressId(billingAddress)
                    .totalPrice(BigDecimal.valueOf(100))
                    .orderStatus(OrderStatus.PENDING)
                    .paymentStatus(PaymentStatus.PENDING)
                    .build();
            order.setCreatedOn(null);

            OrderBriefVm result = OrderBriefVm.fromModel(order);

            assertNotNull(result);
            assertNull(result.createdOn());
            assertEquals(1L, result.id());
            assertNotNull(result.billingAddressVm());
        }
    }
}
