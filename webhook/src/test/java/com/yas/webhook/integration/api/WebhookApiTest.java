package com.yas.webhook.integration.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestBodySpec;
import org.springframework.web.client.RestClient.RequestBodyUriSpec;
import org.springframework.web.client.RestClient.ResponseSpec;

@ExtendWith(MockitoExtension.class)
class WebhookApiTest {

    @Mock
    RestClient restClient;

    WebhookApi webhookApi;

    @BeforeEach
    void setUp() {
        webhookApi = new WebhookApi(restClient);
    }

    @Test
    void test_notify_withSecret_shouldAddHeader() {
        String url = "http://example.com";
        String secret = "my-secret";
        JsonNode payload = new ObjectMapper().createObjectNode();

        RequestBodyUriSpec requestBodyUriSpec = mock(RequestBodyUriSpec.class);
        RequestBodySpec requestBodySpec = mock(RequestBodySpec.class);
        ResponseSpec responseSpec = mock(ResponseSpec.class);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(url)).thenReturn(requestBodySpec);
        when(requestBodySpec.header(eq(WebhookApi.X_HUB_SIGNATURE_256), any(String.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.body(payload)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        webhookApi.notify(url, secret, payload);

        verify(requestBodySpec).header(eq(WebhookApi.X_HUB_SIGNATURE_256), any(String.class));
        verify(requestBodySpec).body(payload);
    }

    @Test
    void test_notify_withoutSecret_shouldNotAddHeader() {
        String url = "http://example.com";
        String secret = null;
        JsonNode payload = new ObjectMapper().createObjectNode();

        RequestBodyUriSpec requestBodyUriSpec = mock(RequestBodyUriSpec.class);
        RequestBodySpec requestBodySpec = mock(RequestBodySpec.class);
        ResponseSpec responseSpec = mock(ResponseSpec.class);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(url)).thenReturn(requestBodySpec);
        when(requestBodySpec.body(payload)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        webhookApi.notify(url, secret, payload);

        verify(requestBodySpec, org.mockito.Mockito.never()).header(eq(WebhookApi.X_HUB_SIGNATURE_256), any(String.class));
        verify(requestBodySpec).body(payload);
    }
}
