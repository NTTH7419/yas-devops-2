package com.yas.inventory.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    // Concrete subclass to test the abstract class
    static class TestHandler extends AbstractCircuitBreakFallbackHandler {
    }

    private final TestHandler handler = new TestHandler();

    @Nested
    class HandleTypedFallbackTests {

        @Test
        void handleTypedFallback_shouldRethrowException_whenCalledWithRuntimeException() {
            RuntimeException ex = new RuntimeException("Circuit breaker triggered");

            assertThatThrownBy(() -> handler.handleTypedFallback(ex))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Circuit breaker triggered");
        }

        @Test
        void handleTypedFallback_shouldRethrowException_whenCalledWithGenericThrowable() {
            Throwable ex = new IllegalStateException("Something went wrong");

            assertThatThrownBy(() -> handler.handleTypedFallback(ex))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Something went wrong");
        }
    }

    @Nested
    class HandleBodilessFallbackTests {

        @Test
        void handleBodilessFallback_shouldRethrowException_whenCalledWithException() {
            Exception ex = new Exception("Service unavailable");

            assertThatThrownBy(() -> handler.handleBodilessFallback(ex))
                .isInstanceOf(Exception.class)
                .hasMessage("Service unavailable");
        }

        @Test
        void handleBodilessFallback_shouldRethrowException_whenCalledWithRuntimeException() {
            RuntimeException ex = new RuntimeException("Fallback error");

            assertThatThrownBy(() -> handler.handleBodilessFallback(ex))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Fallback error");
        }
    }
}
