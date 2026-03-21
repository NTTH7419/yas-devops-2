package com.yas.webhook.integration.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@ExtendWith(MockitoExtension.class)
class WebhookApiTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private WebhookApi webhookApi;

    @Test
    void notify_shouldSendPostRequestWithoutSignatureWhenSecretIsEmpty() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("key", "value");

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(JsonNode.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        webhookApi.notify("https://example.com/webhook", "", payload);

        verify(restClient).post();
        verify(requestBodyUriSpec).uri("https://example.com/webhook");
        verify(requestBodySpec).body(payload);
        verify(requestBodySpec).retrieve();
    }

    @Test
    void notify_shouldSendPostRequestWithSignatureWhenSecretIsProvided() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("key", "value");

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(JsonNode.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        webhookApi.notify("https://example.com/webhook", "my-secret", payload);

        verify(restClient).post();
        verify(requestBodyUriSpec).uri("https://example.com/webhook");
        verify(requestBodySpec).header(eq(WebhookApi.X_HUB_SIGNATURE_256), anyString());
        verify(requestBodySpec).body(payload);
        verify(requestBodySpec).retrieve();
    }

    @Test
    void notify_shouldNotAddHeaderWhenSecretIsNull() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("key", "value");

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(JsonNode.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        webhookApi.notify("https://example.com/webhook", null, payload);

        verify(requestBodySpec, org.mockito.Mockito.never())
                .header(anyString(), anyString());
    }

    @Test
    void notify_shouldSendToCorrectUrl() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode payload = objectMapper.createObjectNode();

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("https://custom-url.com/hooks/receive")).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(JsonNode.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        webhookApi.notify("https://custom-url.com/hooks/receive", "", payload);

        verify(requestBodyUriSpec).uri("https://custom-url.com/hooks/receive");
    }
}
