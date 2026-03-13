package com.yas.payment.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.yas.payment.model.PaymentProvider;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class CreatePaymentProviderMapperTest {

    private CreatePaymentProviderMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(CreatePaymentProviderMapper.class);
    }

    @Test
    @DisplayName("toModel should map all fields from CreatePaymentVm to PaymentProvider")
    void toModel_ShouldMapAllFields() {
        CreatePaymentVm vm = new CreatePaymentVm();
        vm.setId("paypal");
        vm.setEnabled(true);
        vm.setName("PayPal");
        vm.setConfigureUrl("http://configure.url");
        vm.setLandingViewComponentName("PaypalLanding");
        vm.setAdditionalSettings("{\"clientId\":\"abc\"}");
        vm.setMediaId(10L);

        PaymentProvider model = mapper.toModel(vm);

        assertNotNull(model);
        assertThat(model.getId()).isEqualTo("paypal");
        assertThat(model.isEnabled()).isTrue();
        assertThat(model.getName()).isEqualTo("PayPal");
        assertThat(model.getConfigureUrl()).isEqualTo("http://configure.url");
        assertThat(model.getLandingViewComponentName()).isEqualTo("PaypalLanding");
        assertThat(model.getAdditionalSettings()).isEqualTo("{\"clientId\":\"abc\"}");
        assertThat(model.getMediaId()).isEqualTo(10L);
        // isNew should be set to true per the @Mapping annotation
        assertThat(model.isNew()).isTrue();
    }

    @Test
    @DisplayName("toModel with minimal fields should not throw")
    void toModel_WithMinimalFields_ShouldNotThrow() {
        CreatePaymentVm vm = new CreatePaymentVm();
        vm.setId("cod");
        vm.setName("COD");
        vm.setConfigureUrl("http://cod.url");

        PaymentProvider model = mapper.toModel(vm);

        assertNotNull(model);
        assertThat(model.getId()).isEqualTo("cod");
        assertThat(model.isNew()).isTrue();
        assertNull(model.getAdditionalSettings());
        assertNull(model.getMediaId());
    }

    @Test
    @DisplayName("toVmResponse should map PaymentProvider to PaymentProviderVm")
    void toVmResponse_ShouldMapAllFields() {
        PaymentProvider provider = new PaymentProvider();
        provider.setId("paypal");
        provider.setEnabled(true);
        provider.setName("PayPal");
        provider.setConfigureUrl("http://configure.url");
        provider.setLandingViewComponentName("PaypalLanding");
        provider.setAdditionalSettings("{\"clientId\":\"abc\"}");
        provider.setMediaId(10L);

        PaymentProviderVm vm = mapper.toVmResponse(provider);

        assertNotNull(vm);
        assertThat(vm.getId()).isEqualTo("paypal");
        assertThat(vm.getName()).isEqualTo("PayPal");
        assertThat(vm.getConfigureUrl()).isEqualTo("http://configure.url");
        assertThat(vm.getMediaId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("toVm should map PaymentProvider to CreatePaymentVm")
    void toVm_ShouldMapAllFields() {
        PaymentProvider provider = new PaymentProvider();
        provider.setId("paypal");
        provider.setEnabled(false);
        provider.setName("PayPal");
        provider.setConfigureUrl("http://configure.url");
        provider.setLandingViewComponentName("PaypalLanding");
        provider.setAdditionalSettings("{\"key\":\"val\"}");
        provider.setMediaId(5L);

        CreatePaymentVm vm = mapper.toVm(provider);

        assertNotNull(vm);
        assertThat(vm.getId()).isEqualTo("paypal");
        assertThat(vm.isEnabled()).isFalse();
        assertThat(vm.getName()).isEqualTo("PayPal");
        assertThat(vm.getConfigureUrl()).isEqualTo("http://configure.url");
        assertThat(vm.getAdditionalSettings()).isEqualTo("{\"key\":\"val\"}");
        assertThat(vm.getMediaId()).isEqualTo(5L);
    }

    @Test
    @DisplayName("partialUpdate should update non-null fields from vm to model")
    void partialUpdate_ShouldUpdateNonNullFields() {
        PaymentProvider model = new PaymentProvider();
        model.setId("paypal");
        model.setName("PayPal Old");
        model.setConfigureUrl("http://old.url");
        model.setEnabled(true);

        CreatePaymentVm vm = new CreatePaymentVm();
        vm.setId("paypal");
        vm.setName("PayPal New");
        vm.setConfigureUrl("http://new.url");

        mapper.partialUpdate(model, vm);

        assertThat(model.getName()).isEqualTo("PayPal New");
        assertThat(model.getConfigureUrl()).isEqualTo("http://new.url");
    }
}
