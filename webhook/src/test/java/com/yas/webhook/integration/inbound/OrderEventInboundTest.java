package com.yas.webhook.integration.inbound;

import static org.mockito.Mockito.verify;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.yas.webhook.service.OrderEventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderEventInboundTest {

    @Mock
    OrderEventService orderEventService;

    @InjectMocks
    OrderEventInbound orderEventInbound;

    @Test
    void test_onOrderEvent_shouldCallService() {
        JsonNode payload = new ObjectMapper().createObjectNode();
        orderEventInbound.onOrderEvent(payload);
        verify(orderEventService).onOrderEvent(payload);
    }
}
