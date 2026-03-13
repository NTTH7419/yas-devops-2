package com.yas.payment.service.provider.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.payment.model.CapturedPayment;
import com.yas.payment.model.InitiatedPayment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.paypal.service.PaypalService;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentResponse;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentResponse;
import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaypalHandlerTest {

    @Mock
    private PaymentProviderService paymentProviderService;

    @Mock
    private PaypalService paypalService;

    private PaypalHandler paypalHandler;

    private static final String ADDITIONAL_SETTINGS = "{\"clientId\":\"test\",\"clientSecret\":\"secret\"}";

    @BeforeEach
    void setUp() {
        paypalHandler = new PaypalHandler(paymentProviderService, paypalService);
    }

    @Test
    @DisplayName("getProviderId should return PAYPAL")
    void getProviderId_ShouldReturnPaypal() {
        String providerId = paypalHandler.getProviderId();
        assertEquals(PaymentMethod.PAYPAL.name(), providerId);
    }

    @Test
    @DisplayName("initPayment should delegate to PaypalService and return InitiatedPayment")
    void initPayment_ShouldDelegateToPaypalServiceAndBuildInitiatedPayment() {
        // Given
        InitPaymentRequestVm request = InitPaymentRequestVm.builder()
            .paymentMethod(PaymentMethod.PAYPAL.name())
            .totalPrice(BigDecimal.valueOf(100.0))
            .checkoutId("checkout-001")
            .build();

        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(PaymentMethod.PAYPAL.name()))
            .thenReturn(ADDITIONAL_SETTINGS);

        PaypalCreatePaymentResponse paypalResponse = PaypalCreatePaymentResponse.builder()
            .status("CREATED")
            .paymentId("paypal-pay-001")
            .redirectUrl("https://paypal.com/redirect")
            .build();

        when(paypalService.createPayment(any(PaypalCreatePaymentRequest.class))).thenReturn(paypalResponse);

        // When
        InitiatedPayment result = paypalHandler.initPayment(request);

        // Then
        assertNotNull(result);
        assertEquals("CREATED", result.getStatus());
        assertEquals("paypal-pay-001", result.getPaymentId());
        assertEquals("https://paypal.com/redirect", result.getRedirectUrl());

        verify(paypalService, times(1)).createPayment(any(PaypalCreatePaymentRequest.class));
        verify(paymentProviderService, times(1)).getAdditionalSettingsByPaymentProviderId(PaymentMethod.PAYPAL.name());
    }

    @Test
    @DisplayName("capturePayment should delegate to PaypalService and return CapturedPayment")
    void capturePayment_ShouldDelegateToPaypalServiceAndBuildCapturedPayment() {
        // Given
        CapturePaymentRequestVm request = CapturePaymentRequestVm.builder()
            .paymentMethod(PaymentMethod.PAYPAL.name())
            .token("token-123")
            .build();

        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(PaymentMethod.PAYPAL.name()))
            .thenReturn(ADDITIONAL_SETTINGS);

        PaypalCapturePaymentResponse paypalResponse = PaypalCapturePaymentResponse.builder()
            .checkoutId("checkout-001")
            .amount(BigDecimal.valueOf(100.0))
            .paymentFee(BigDecimal.valueOf(2.5))
            .gatewayTransactionId("gw-txn-001")
            .paymentMethod(PaymentMethod.PAYPAL.name())
            .paymentStatus(PaymentStatus.COMPLETED.name())
            .failureMessage(null)
            .build();

        when(paypalService.capturePayment(any(PaypalCapturePaymentRequest.class))).thenReturn(paypalResponse);

        // When
        CapturedPayment result = paypalHandler.capturePayment(request);

        // Then
        assertNotNull(result);
        assertEquals("checkout-001", result.getCheckoutId());
        assertEquals(BigDecimal.valueOf(100.0), result.getAmount());
        assertEquals(BigDecimal.valueOf(2.5), result.getPaymentFee());
        assertEquals("gw-txn-001", result.getGatewayTransactionId());
        assertEquals(PaymentMethod.PAYPAL, result.getPaymentMethod());
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());

        verify(paypalService, times(1)).capturePayment(any(PaypalCapturePaymentRequest.class));
        verify(paymentProviderService, times(1)).getAdditionalSettingsByPaymentProviderId(PaymentMethod.PAYPAL.name());
    }

    @Test
    @DisplayName("capturePayment with CANCELLED status should map correctly")
    void capturePayment_WithCancelledStatus_ShouldMapCorrectly() {
        // Given
        CapturePaymentRequestVm request = CapturePaymentRequestVm.builder()
            .paymentMethod(PaymentMethod.PAYPAL.name())
            .token("cancelled-token")
            .build();

        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(PaymentMethod.PAYPAL.name()))
            .thenReturn(ADDITIONAL_SETTINGS);

        PaypalCapturePaymentResponse paypalResponse = PaypalCapturePaymentResponse.builder()
            .checkoutId("checkout-002")
            .amount(BigDecimal.valueOf(50.0))
            .paymentFee(BigDecimal.ZERO)
            .gatewayTransactionId("gw-txn-cancelled")
            .paymentMethod(PaymentMethod.PAYPAL.name())
            .paymentStatus(PaymentStatus.CANCELLED.name())
            .failureMessage("Payment was cancelled")
            .build();

        when(paypalService.capturePayment(any(PaypalCapturePaymentRequest.class))).thenReturn(paypalResponse);

        // When
        CapturedPayment result = paypalHandler.capturePayment(request);

        // Then
        assertNotNull(result);
        assertEquals(PaymentStatus.CANCELLED, result.getPaymentStatus());
        assertEquals("Payment was cancelled", result.getFailureMessage());
    }
}
