package com.yas.payment.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.CapturePaymentResponseVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentResponseVm;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @InjectMocks
    private PaymentController paymentController;

    @Mock
    private PaymentService paymentService;

    private InitPaymentRequestVm initPaymentRequestVm;
    private CapturePaymentRequestVm capturePaymentRequestVm;

    @BeforeEach
    void setUp() {
        initPaymentRequestVm = InitPaymentRequestVm.builder()
            .paymentMethod(PaymentMethod.PAYPAL.name())
            .totalPrice(BigDecimal.TEN)
            .checkoutId("checkout-001")
            .build();

        capturePaymentRequestVm = CapturePaymentRequestVm.builder()
            .paymentMethod(PaymentMethod.PAYPAL.name())
            .token("token-001")
            .build();
    }

    @Test
    @DisplayName("initPayment should return InitPaymentResponseVm from service")
    void initPayment_ShouldReturnResponse_WhenSuccess() {
        InitPaymentResponseVm expectedResponse = InitPaymentResponseVm.builder()
            .paymentId("pay-001")
            .status("CREATED")
            .redirectUrl("http://redirect.url")
            .build();

        when(paymentService.initPayment(initPaymentRequestVm)).thenReturn(expectedResponse);

        InitPaymentResponseVm result = paymentController.initPayment(initPaymentRequestVm);

        assertNotNull(result);
        assertEquals(expectedResponse.paymentId(), result.paymentId());
        assertEquals(expectedResponse.status(), result.status());
        assertEquals(expectedResponse.redirectUrl(), result.redirectUrl());

        verify(paymentService, times(1)).initPayment(initPaymentRequestVm);
    }

    @Test
    @DisplayName("capturePayment should return CapturePaymentResponseVm from service")
    void capturePayment_ShouldReturnResponse_WhenSuccess() {
        CapturePaymentResponseVm expectedResponse = CapturePaymentResponseVm.builder()
            .orderId(1L)
            .checkoutId("checkout-001")
            .amount(BigDecimal.TEN)
            .paymentFee(BigDecimal.ONE)
            .gatewayTransactionId("gw-txn-001")
            .paymentMethod(PaymentMethod.PAYPAL)
            .paymentStatus(PaymentStatus.COMPLETED)
            .failureMessage(null)
            .build();

        when(paymentService.capturePayment(capturePaymentRequestVm)).thenReturn(expectedResponse);

        CapturePaymentResponseVm result = paymentController.capturePayment(capturePaymentRequestVm);

        assertNotNull(result);
        assertEquals(expectedResponse.orderId(), result.orderId());
        assertEquals(expectedResponse.checkoutId(), result.checkoutId());
        assertEquals(expectedResponse.amount(), result.amount());
        assertEquals(expectedResponse.paymentStatus(), result.paymentStatus());

        verify(paymentService, times(1)).capturePayment(capturePaymentRequestVm);
    }

    @Test
    @DisplayName("cancelPayment should return 200 OK with cancel message")
    void cancelPayment_ShouldReturn200_WithMessage() {
        ResponseEntity<String> response = paymentController.cancelPayment();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Payment cancelled", response.getBody());
    }
}
