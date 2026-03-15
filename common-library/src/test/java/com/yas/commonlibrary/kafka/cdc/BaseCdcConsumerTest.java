package com.yas.commonlibrary.kafka.cdc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.mockito.Mockito.verify;

class BaseCdcConsumerTest {

    private TestCdcConsumer consumer;

    @Mock
    private Consumer<String> mockConsumer;

    @Mock
    private BiConsumer<String, String> mockBiConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        consumer = new TestCdcConsumer();
    }

    @Test
    void processMessage_withConsumer_shouldCallAccept() {
        String record = "test-record";
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put(KafkaHeaders.RECEIVED_KEY, "test-key");
        MessageHeaders headers = new MessageHeaders(headerMap);

        consumer.processMessage(record, headers, mockConsumer);

        verify(mockConsumer).accept(record);
    }

    @Test
    void processMessage_withBiConsumer_shouldCallAccept() {
        String key = "test-key";
        String value = "test-value";
        Map<String, Object> headerMap = new HashMap<>();
        MessageHeaders headers = new MessageHeaders(headerMap);

        consumer.processMessage(key, value, headers, mockBiConsumer);

        verify(mockBiConsumer).accept(key, value);
    }

    private static class TestCdcConsumer extends BaseCdcConsumer<String, String> {
        @Override
        protected void processMessage(String record, MessageHeaders headers, Consumer<String> consumer) {
            super.processMessage(record, headers, consumer);
        }

        @Override
        protected void processMessage(String key, String value, MessageHeaders headers, BiConsumer<String, String> consumer) {
            super.processMessage(key, value, headers, consumer);
        }
    }
}
