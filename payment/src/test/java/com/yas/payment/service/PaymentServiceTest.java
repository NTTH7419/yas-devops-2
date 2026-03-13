package com.yas.payment.service;

import com.yas.payment.model.CapturedPayment;
import com.yas.payment.model.InitiatedPayment;
import com.yas.payment.model.Payment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.repository.PaymentRepository;
import com.yas.payment.service.provider.handler.PaymentHandler;
import com.yas.payment.service.provider.handler.PaypalHandler;
import com.yas.payment.viewmodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    private PaymentRepository paymentRepository;
    private OrderService orderService;
    private PaymentHandler paymentHandler;
    private List<PaymentHandler> paymentHandlers = new ArrayList<>();
    private PaymentService paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        orderService = mock(OrderService.class);
        paymentHandler = mock(PaymentHandler.class);
        paymentHandlers.add(paymentHandler);
        paymentService = new PaymentService(paymentRepository, orderService, paymentHandlers);

        when(paymentHandler.getProviderId()).thenReturn(PaymentMethod.PAYPAL.name());
        paymentService.initializeProviders();

        payment = new Payment();
        payment.setId(1L);
        payment.setCheckoutId("secretCheckoutId");
        payment.setOrderId(2L);
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaymentFee(BigDecimal.valueOf(500));
        payment.setPaymentMethod(PaymentMethod.BANKING);
        payment.setAmount(BigDecimal.valueOf(100.0));
        payment.setFailureMessage(null);
        payment.setGatewayTransactionId("gatewayId");
    }

    @Test
    void initPayment_Success() {
        InitPaymentRequestVm initPaymentRequestVm = InitPaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name()).totalPrice(BigDecimal.TEN).checkoutId("123").build();
        InitiatedPayment initiatedPayment = InitiatedPayment.builder().paymentId("123").status("success").redirectUrl("http://abc.com").build();
        when(paymentHandler.initPayment(initPaymentRequestVm)).thenReturn(initiatedPayment);
        InitPaymentResponseVm result = paymentService.initPayment(initPaymentRequestVm);
        assertEquals(initiatedPayment.getPaymentId(), result.paymentId());
        assertEquals(initiatedPayment.getStatus(), result.status());
        assertEquals(initiatedPayment.getRedirectUrl(), result.redirectUrl());
    }

    @Test
    void capturePayment_Success() {
        CapturePaymentRequestVm capturePaymentRequestVM = CapturePaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name()).token("123").build();
        CapturedPayment capturedPayment = prepareCapturedPayment();
        Long orderId = 999L;
        when(paymentHandler.capturePayment(capturePaymentRequestVM)).thenReturn(capturedPayment);
        when(orderService.updateCheckoutStatus(capturedPayment)).thenReturn(orderId);
        when(paymentRepository.save(any())).thenReturn(payment);
        CapturePaymentResponseVm capturePaymentResponseVm = paymentService.capturePayment(capturePaymentRequestVM);
        verifyPaymentCreation(capturePaymentResponseVm);
        verifyOrderServiceInteractions(capturedPayment);
        verifyResult(capturedPayment, capturePaymentResponseVm);
    }

    private CapturedPayment prepareCapturedPayment() {
        return CapturedPayment.builder()
            .orderId(2L)
            .checkoutId("secretCheckoutId")
            .amount(BigDecimal.valueOf(100.0))
            .paymentFee(BigDecimal.valueOf(500))
            .gatewayTransactionId("gatewayId")
            .paymentMethod(PaymentMethod.BANKING)
            .paymentStatus(PaymentStatus.COMPLETED)
            .failureMessage(null)
            .build();
    }

    private void verifyPaymentCreation(CapturePaymentResponseVm capturedPayment) {
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());
        Payment capturedPaymentResult = paymentCaptor.getValue();

        assertThat(capturedPaymentResult.getCheckoutId()).isEqualTo(capturedPayment.checkoutId());
        assertThat(capturedPaymentResult.getOrderId()).isEqualTo(capturedPayment.orderId());
        assertThat(capturedPaymentResult.getPaymentStatus()).isEqualTo(capturedPayment.paymentStatus());
        assertThat(capturedPaymentResult.getPaymentFee()).isEqualByComparingTo(capturedPayment.paymentFee());
        assertThat(capturedPaymentResult.getAmount()).isEqualByComparingTo(capturedPayment.amount());
    }

    private void verifyOrderServiceInteractions(CapturedPayment capturedPayment) {
        verify(orderService, times(1)).updateCheckoutStatus((capturedPayment));
        verify(orderService, times(1)).updateOrderStatus(any(PaymentOrderStatusVm.class));
    }

    private void verifyResult(CapturedPayment capturedPayment, CapturePaymentResponseVm responseVm) {
        assertEquals(capturedPayment.getOrderId(), responseVm.orderId());
        assertEquals(capturedPayment.getCheckoutId(), responseVm.checkoutId());
        assertEquals(capturedPayment.getAmount(), responseVm.amount());
        assertEquals(capturedPayment.getPaymentFee(), responseVm.paymentFee());
        assertEquals(capturedPayment.getGatewayTransactionId(), responseVm.gatewayTransactionId());
        assertEquals(capturedPayment.getPaymentMethod(), responseVm.paymentMethod());
        assertEquals(capturedPayment.getPaymentStatus(), responseVm.paymentStatus());
        assertEquals(capturedPayment.getFailureMessage(), responseVm.failureMessage());
    }

    @Test
    void initPayment_WhenUnknownProvider_ShouldThrowIllegalArgumentException() {
        InitPaymentRequestVm requestVm = InitPaymentRequestVm.builder()
                .paymentMethod("UNKNOWN_PROVIDER")
                .totalPrice(BigDecimal.TEN)
                .checkoutId("checkout-999")
                .build();

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> paymentService.initPayment(requestVm)
        );

        assertThat(exception.getMessage()).contains("No payment handler found for provider: UNKNOWN_PROVIDER");
    }

    @Test
    void capturePayment_WhenUnknownProvider_ShouldThrowIllegalArgumentException() {
        CapturePaymentRequestVm requestVm = CapturePaymentRequestVm.builder()
                .paymentMethod("UNKNOWN_PROVIDER")
                .token("token-999")
                .build();

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> paymentService.capturePayment(requestVm)
        );

        assertThat(exception.getMessage()).contains("No payment handler found for provider: UNKNOWN_PROVIDER");
    }

    @Test
    void initializeProviders_ShouldRegisterHandlerForPaypal() {
        // Handler has already been registered in setUp()
        // Try calling initPayment with PAYPAL - should not throw
        InitPaymentRequestVm requestVm = InitPaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .totalPrice(BigDecimal.TEN)
                .checkoutId("checkout-init")
                .build();
        InitiatedPayment initiated = InitiatedPayment.builder()
                .paymentId("pid").status("CREATED").redirectUrl("http://url").build();
        when(paymentHandler.initPayment(requestVm)).thenReturn(initiated);

        InitPaymentResponseVm result = paymentService.initPayment(requestVm);
        assertThat(result.paymentId()).isEqualTo("pid");
    }
}
