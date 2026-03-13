package com.yas.payment.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.payment.model.PaymentProvider;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class PaymentProviderMapperTest {

    private PaymentProviderMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(PaymentProviderMapper.class);
    }

    @Test
    @DisplayName("toVm should map PaymentProvider to PaymentProviderVm correctly")
    void toVm_ShouldMapAllFields() {
        PaymentProvider provider = new PaymentProvider();
        provider.setId("paypal");
        provider.setEnabled(true);
        provider.setName("PayPal");
        provider.setConfigureUrl("http://configure.url");
        provider.setLandingViewComponentName("PaypalLanding");
        provider.setAdditionalSettings("{\"clientId\":\"abc\"}");
        provider.setMediaId(100L);

        PaymentProviderVm vm = mapper.toVm(provider);

        assertNotNull(vm);
        assertThat(vm.getId()).isEqualTo("paypal");
        assertThat(vm.getName()).isEqualTo("PayPal");
        assertThat(vm.getConfigureUrl()).isEqualTo("http://configure.url");
        assertThat(vm.getMediaId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("toVm should handle null mediaId")
    void toVm_ShouldHandleNullMediaId() {
        PaymentProvider provider = new PaymentProvider();
        provider.setId("cod");
        provider.setEnabled(true);
        provider.setName("COD");
        provider.setConfigureUrl("http://cod.url");
        provider.setMediaId(null);

        PaymentProviderVm vm = mapper.toVm(provider);

        assertNotNull(vm);
        assertThat(vm.getId()).isEqualTo("cod");
        assertThat(vm.getMediaId()).isNull();
    }

    @Test
    @DisplayName("toModel should map PaymentProviderVm to PaymentProvider")
    void toModel_ShouldMapAllFields() {
        PaymentProviderVm vm = new PaymentProviderVm("paypal", "PayPal", "http://configure.url", 0, 100L, null);

        PaymentProvider model = mapper.toModel(vm);

        assertNotNull(model);
        assertThat(model.getId()).isEqualTo("paypal");
        assertThat(model.getName()).isEqualTo("PayPal");
        assertThat(model.getConfigureUrl()).isEqualTo("http://configure.url");
        assertThat(model.getMediaId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("partialUpdate should update non-null fields only")
    void partialUpdate_ShouldUpdateNonNullFields() {
        PaymentProvider model = new PaymentProvider();
        model.setId("paypal");
        model.setName("Old Name");
        model.setConfigureUrl("http://old.url");

        PaymentProviderVm vm = new PaymentProviderVm("paypal", "New Name", "http://new.url", 0, null, null);

        mapper.partialUpdate(model, vm);

        assertThat(model.getName()).isEqualTo("New Name");
        assertThat(model.getConfigureUrl()).isEqualTo("http://new.url");
    }
}
