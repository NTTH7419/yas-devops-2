package com.yas.order.viewmodel.orderaddress;

import static org.junit.jupiter.api.Assertions.*;

import com.yas.order.model.OrderAddress;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrderAddressVmTest {

    @Nested
    class FromModelTests {

        @Test
        void fromModel_shouldCreateOrderAddressVmWithAllFields() {
            OrderAddress orderAddress = OrderAddress.builder()
                    .id(1L)
                    .contactName("John Doe")
                    .phone("123456789")
                    .addressLine1("123 Main St")
                    .addressLine2("Apt 4")
                    .city("Hanoi")
                    .zipCode("10000")
                    .districtId(5L)
                    .districtName("Ba Dinh")
                    .stateOrProvinceId(10L)
                    .stateOrProvinceName("Hanoi City")
                    .countryId(1L)
                    .countryName("Vietnam")
                    .build();

            OrderAddressVm result = OrderAddressVm.fromModel(orderAddress);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("John Doe", result.contactName());
            assertEquals("123456789", result.phone());
            assertEquals("123 Main St", result.addressLine1());
            assertEquals("Apt 4", result.addressLine2());
            assertEquals("Hanoi", result.city());
            assertEquals("10000", result.zipCode());
            assertEquals(5L, result.districtId());
            assertEquals("Ba Dinh", result.districtName());
            assertEquals(10L, result.stateOrProvinceId());
            assertEquals("Hanoi City", result.stateOrProvinceName());
            assertEquals(1L, result.countryId());
            assertEquals("Vietnam", result.countryName());
        }

        @Test
        void fromModel_shouldHandleNullFields() {
            OrderAddress orderAddress = OrderAddress.builder()
                    .id(1L)
                    .contactName(null)
                    .phone(null)
                    .addressLine1(null)
                    .addressLine2(null)
                    .city(null)
                    .zipCode(null)
                    .districtId(null)
                    .districtName(null)
                    .stateOrProvinceId(null)
                    .stateOrProvinceName(null)
                    .countryId(null)
                    .countryName(null)
                    .build();

            OrderAddressVm result = OrderAddressVm.fromModel(orderAddress);

            assertNotNull(result);
            assertEquals(1L, result.id());
            assertNull(result.contactName());
            assertNull(result.phone());
            assertNull(result.addressLine1());
            assertNull(result.addressLine2());
            assertNull(result.city());
            assertNull(result.zipCode());
            assertNull(result.districtId());
            assertNull(result.districtName());
            assertNull(result.stateOrProvinceId());
            assertNull(result.stateOrProvinceName());
            assertNull(result.countryId());
            assertNull(result.countryName());
        }

        @Test
        void fromModel_shouldHandleEmptyOptionalFields() {
            OrderAddress orderAddress = OrderAddress.builder()
                    .id(99L)
                    .contactName("")
                    .phone("")
                    .addressLine1("")
                    .city("")
                    .zipCode("")
                    .countryName("")
                    .build();

            OrderAddressVm result = OrderAddressVm.fromModel(orderAddress);

            assertNotNull(result);
            assertEquals(99L, result.id());
        }
    }
}
