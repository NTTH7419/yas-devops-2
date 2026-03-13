package com.yas.payment.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.payment.model.PaymentProvider;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UpdatePaymentProviderMapperTest {

    private UpdatePaymentProviderMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UpdatePaymentProviderMapper.class);
    }

    @Test
    @DisplayName("toModel should map UpdatePaymentVm to PaymentProvider")
    void toModel_ShouldMapAllFields() {
        UpdatePaymentVm vm = new UpdatePaymentVm();
        vm.setId("paypal");
        vm.setEnabled(true);
        vm.setName("PayPal");
        vm.setConfigureUrl("http://configure.url");
        vm.setLandingViewComponentName("PaypalLanding");
        vm.setAdditionalSettings("{\"clientId\":\"xyz\"}");
        vm.setMediaId(7L);

        PaymentProvider model = mapper.toModel(vm);

        assertNotNull(model);
        assertThat(model.getId()).isEqualTo("paypal");
        assertThat(model.isEnabled()).isTrue();
        assertThat(model.getName()).isEqualTo("PayPal");
        assertThat(model.getConfigureUrl()).isEqualTo("http://configure.url");
        assertThat(model.getLandingViewComponentName()).isEqualTo("PaypalLanding");
        assertThat(model.getAdditionalSettings()).isEqualTo("{\"clientId\":\"xyz\"}");
        assertThat(model.getMediaId()).isEqualTo(7L);
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
        provider.setMediaId(99L);

        PaymentProviderVm vm = mapper.toVmResponse(provider);

        assertNotNull(vm);
        assertThat(vm.getId()).isEqualTo("paypal");
        assertThat(vm.getName()).isEqualTo("PayPal");
        assertThat(vm.getConfigureUrl()).isEqualTo("http://configure.url");
        assertThat(vm.getMediaId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("toVm should map PaymentProvider to UpdatePaymentVm")
    void toVm_ShouldMapAllFields() {
        PaymentProvider provider = new PaymentProvider();
        provider.setId("cod");
        provider.setEnabled(false);
        provider.setName("COD");
        provider.setConfigureUrl("http://cod.url");
        provider.setLandingViewComponentName("CodLanding");
        provider.setAdditionalSettings(null);
        provider.setMediaId(null);

        UpdatePaymentVm vm = mapper.toVm(provider);

        assertNotNull(vm);
        assertThat(vm.getId()).isEqualTo("cod");
        assertThat(vm.isEnabled()).isFalse();
        assertThat(vm.getName()).isEqualTo("COD");
        assertThat(vm.getConfigureUrl()).isEqualTo("http://cod.url");
        assertThat(vm.getAdditionalSettings()).isNull();
        assertThat(vm.getMediaId()).isNull();
    }

    @Test
    @DisplayName("partialUpdate should only update non-null fields")
    void partialUpdate_ShouldUpdateOnlyNonNullFields() {
        PaymentProvider model = new PaymentProvider();
        model.setId("paypal");
        model.setName("Old PayPal");
        model.setConfigureUrl("http://old.url");
        model.setLandingViewComponentName("OldLanding");
        model.setEnabled(true);

        UpdatePaymentVm vm = new UpdatePaymentVm();
        vm.setId("paypal");
        vm.setName("Updated PayPal");
        vm.setConfigureUrl("http://new.url");
        vm.setLandingViewComponentName("NewLanding");
        vm.setEnabled(false);

        mapper.partialUpdate(model, vm);

        assertThat(model.getName()).isEqualTo("Updated PayPal");
        assertThat(model.getConfigureUrl()).isEqualTo("http://new.url");
        assertThat(model.getLandingViewComponentName()).isEqualTo("NewLanding");
        assertThat(model.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("partialUpdate with null optional fields should keep original values")
    void partialUpdate_WhenOptionalFieldsAreNull_ShouldKeepOriginalValues() {
        PaymentProvider model = new PaymentProvider();
        model.setId("paypal");
        model.setName("PayPal");
        model.setConfigureUrl("http://configure.url");
        model.setAdditionalSettings("existingSettings");
        model.setMediaId(5L);

        UpdatePaymentVm vm = new UpdatePaymentVm();
        vm.setId("paypal");
        vm.setName("PayPal");
        vm.setConfigureUrl("http://configure.url");
        // additionalSettings and mediaId are null in vm

        mapper.partialUpdate(model, vm);

        // Since NullValuePropertyMappingStrategy.IGNORE, null fields in vm should not override
        assertThat(model.getAdditionalSettings()).isEqualTo("existingSettings");
        assertThat(model.getMediaId()).isEqualTo(5L);
    }
}
