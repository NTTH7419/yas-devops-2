package com.yas.order.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private TestableCircuitBreakFallbackHandler handler;

    @BeforeEach
    void setUp() {
        handler = new TestableCircuitBreakFallbackHandler();
    }

    @Nested
    class HandleBodilessFallbackTests {

        @Test
        void handleBodilessFallback_shouldThrowOriginalException() {
            RuntimeException originalException = new RuntimeException("Service unavailable");

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> handler.callHandleBodilessFallback(originalException));

            assertEquals("Service unavailable", thrown.getMessage());
        }

        @Test
        void handleBodilessFallback_shouldLogAndRethrowException() {
            IllegalStateException originalException = new IllegalStateException("Connection failed");

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> handler.callHandleBodilessFallback(originalException));

            assertEquals("Connection failed", thrown.getMessage());
        }

        @Test
        void handleBodilessFallback_shouldHandleNullMessageException() {
            RuntimeException originalException = new RuntimeException();

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> handler.callHandleBodilessFallback(originalException));

            assertNull(thrown.getMessage());
        }
    }

    @Nested
    class HandleTypedFallbackTests {

        @Test
        void handleTypedFallback_shouldThrowOriginalException() {
            RuntimeException originalException = new RuntimeException("Service unavailable");

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> handler.callHandleTypedFallback(originalException));

            assertEquals("Service unavailable", thrown.getMessage());
        }

        @Test
        void handleTypedFallback_shouldThrowAndReturnNull() {
            RuntimeException originalException = new RuntimeException("Test error");

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> handler.callHandleTypedFallback(originalException));

            assertEquals("Test error", thrown.getMessage());
        }

        @Test
        void handleTypedFallback_shouldHandleExceptionWithCause() {
            RuntimeException cause = new RuntimeException("Root cause");
            RuntimeException originalException = new RuntimeException("Wrapper error", cause);

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> handler.callHandleTypedFallback(originalException));

            assertEquals("Wrapper error", thrown.getMessage());
        }
    }

    /**
     * Concrete subclass to test the abstract class.
     */
    static class TestableCircuitBreakFallbackHandler extends AbstractCircuitBreakFallbackHandler {

        public void callHandleBodilessFallback(Throwable throwable) throws Throwable {
            handleBodilessFallback(throwable);
        }

        public Object callHandleTypedFallback(Throwable throwable) throws Throwable {
            return handleTypedFallback(throwable);
        }
    }
}
