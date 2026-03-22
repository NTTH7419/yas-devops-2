package com.yas.webhook.integration.inbound;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import com.yas.webhook.service.OrderEventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@ExtendWith(MockitoExtension.class)
class OrderEventInboundTest {

    @Mock
    private OrderEventService orderEventService;

    @InjectMocks
    private OrderEventInbound orderEventInbound;

    @Test
    void onOrderEvent_shouldDelegateToOrderEventService() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode event = objectMapper.createObjectNode();
        event.put("op", "c");
        event.set("after", objectMapper.createObjectNode());

        orderEventInbound.onOrderEvent(event);

        verify(orderEventService, times(1)).onOrderEvent(event);
    }

    @Test
    void onOrderEvent_shouldPassEventDirectly() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode event = objectMapper.createObjectNode();
        event.put("op", "u");
        ObjectNode before = objectMapper.createObjectNode();
        before.put("order_status", "NEW");
        ObjectNode after = objectMapper.createObjectNode();
        after.put("order_status", "PAID");
        event.set("before", before);
        event.set("after", after);

        orderEventInbound.onOrderEvent(event);

        verify(orderEventService).onOrderEvent(event);
    }
}
