package com.yas.payment.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PaymentProviderControllerTest {

    @InjectMocks
    private PaymentProviderController paymentProviderController;

    @Mock
    private PaymentProviderService paymentProviderService;

    private CreatePaymentVm createPaymentVm;
    private UpdatePaymentVm updatePaymentVm;
    private PaymentProviderVm paymentProviderVm;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        createPaymentVm = new CreatePaymentVm();
        createPaymentVm.setId("paypal");
        createPaymentVm.setEnabled(true);
        createPaymentVm.setName("PayPal");
        createPaymentVm.setConfigureUrl("http://configure.url");
        createPaymentVm.setLandingViewComponentName("PaypalLanding");

        updatePaymentVm = new UpdatePaymentVm();
        updatePaymentVm.setId("paypal");
        updatePaymentVm.setEnabled(false);
        updatePaymentVm.setName("PayPal Updated");
        updatePaymentVm.setConfigureUrl("http://updated.configure.url");
        updatePaymentVm.setLandingViewComponentName("PaypalLandingUpdated");

        paymentProviderVm = new PaymentProviderVm("paypal", "PayPal", "http://configure.url", 0, null, null);

        pageable = Pageable.ofSize(10);
    }

    @Test
    @DisplayName("create should return 201 CREATED with PaymentProviderVm")
    void create_ShouldReturn201_WithCreatedProvider() {
        when(paymentProviderService.create(createPaymentVm)).thenReturn(paymentProviderVm);

        ResponseEntity<PaymentProviderVm> response = paymentProviderController.create(createPaymentVm);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("paypal", response.getBody().getId());

        verify(paymentProviderService, times(1)).create(createPaymentVm);
    }

    @Test
    @DisplayName("update should return 200 OK with updated PaymentProviderVm")
    void update_ShouldReturn200_WithUpdatedProvider() {
        PaymentProviderVm updatedVm = new PaymentProviderVm("paypal", "PayPal Updated", "http://updated.configure.url", 0, null, null);

        when(paymentProviderService.update(updatePaymentVm)).thenReturn(updatedVm);

        ResponseEntity<PaymentProviderVm> response = paymentProviderController.update(updatePaymentVm);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PayPal Updated", response.getBody().getName());

        verify(paymentProviderService, times(1)).update(updatePaymentVm);
    }

    @Test
    @DisplayName("getAll should return 200 OK with list of payment providers")
    void getAll_ShouldReturn200_WithProviderList() {
        List<PaymentProviderVm> providers = List.of(paymentProviderVm);
        when(paymentProviderService.getEnabledPaymentProviders(pageable)).thenReturn(providers);

        ResponseEntity<List<PaymentProviderVm>> response = paymentProviderController.getAll(pageable);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("paypal", response.getBody().get(0).getId());

        verify(paymentProviderService, times(1)).getEnabledPaymentProviders(pageable);
    }

    @Test
    @DisplayName("getAll should return empty list when no providers are enabled")
    void getAll_ShouldReturnEmptyList_WhenNoEnabledProviders() {
        when(paymentProviderService.getEnabledPaymentProviders(pageable)).thenReturn(Collections.emptyList());

        ResponseEntity<List<PaymentProviderVm>> response = paymentProviderController.getAll(pageable);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());

        verify(paymentProviderService, times(1)).getEnabledPaymentProviders(pageable);
    }
}
