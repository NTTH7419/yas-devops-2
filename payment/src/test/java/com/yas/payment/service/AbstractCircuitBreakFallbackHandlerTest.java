package com.yas.payment.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for AbstractCircuitBreakFallbackHandler.
 * Since AbstractCircuitBreakFallbackHandler is abstract (package-private),
 * we test it via the concrete subclasses (OrderService and MediaService)
 * which are already tested in OrderServiceTest and MediaServiceTest.
 * This class tests only the handleError path through handleTypedFallback
 * and handleBodilessFallback on a concrete anonymous subclass that's accessible.
 */
class AbstractCircuitBreakFallbackHandlerTest {

    /**
     * Concrete test subclass since AbstractCircuitBreakFallbackHandler is package-private.
     * We create an anonymous test stub as an inner class.
     */
    private static class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        public <T> T callTypedFallback(Throwable throwable) throws Throwable {
            return handleTypedFallback(throwable);
        }

        public void callBodilessFallback(Throwable throwable) throws Throwable {
            handleBodilessFallback(throwable);
        }
    }

    @Test
    @DisplayName("handleTypedFallback should rethrow the given throwable")
    void handleTypedFallback_ShouldRethrowThrowable() {
        TestFallbackHandler handler = new TestFallbackHandler();
        RuntimeException cause = new RuntimeException("Service unavailable");

        assertThatThrownBy(() -> handler.callTypedFallback(cause))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Service unavailable");
    }

    @Test
    @DisplayName("handleBodilessFallback should rethrow the given throwable")
    void handleBodilessFallback_ShouldRethrowThrowable() {
        TestFallbackHandler handler = new TestFallbackHandler();
        IllegalStateException cause = new IllegalStateException("Circuit open");

        assertThatThrownBy(() -> handler.callBodilessFallback(cause))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Circuit open");
    }

    @Test
    @DisplayName("handleTypedFallback should also rethrow checked exceptions")
    void handleTypedFallback_ShouldRethrowCheckedException() {
        TestFallbackHandler handler = new TestFallbackHandler();
        Exception cause = new Exception("Checked exception");

        assertThatThrownBy(() -> handler.callTypedFallback(cause))
            .isInstanceOf(Exception.class)
            .hasMessage("Checked exception");
    }
}
