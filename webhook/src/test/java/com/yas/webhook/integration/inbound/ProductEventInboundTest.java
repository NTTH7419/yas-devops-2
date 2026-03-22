package com.yas.webhook.integration.inbound;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import com.yas.webhook.service.ProductEventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@ExtendWith(MockitoExtension.class)
class ProductEventInboundTest {

    @Mock
    private ProductEventService productEventService;

    @InjectMocks
    private ProductEventInbound productEventInbound;

    @Test
    void onProductEvent_shouldDelegateToProductEventService() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode event = objectMapper.createObjectNode();
        event.put("op", "u");
        event.set("after", objectMapper.createObjectNode());

        productEventInbound.onProductEvent(event);

        verify(productEventService, times(1)).onProductEvent(event);
    }

    @Test
    void onProductEvent_shouldPassEventDirectly() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode event = objectMapper.createObjectNode();
        event.put("op", "u");
        ObjectNode after = objectMapper.createObjectNode();
        after.put("id", 123);
        after.put("name", "Updated Product");
        event.set("after", after);

        productEventInbound.onProductEvent(event);

        verify(productEventService).onProductEvent(event);
    }
}
