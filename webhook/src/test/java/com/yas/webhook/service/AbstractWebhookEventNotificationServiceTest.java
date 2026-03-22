package com.yas.webhook.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.model.enums.NotificationStatus;
import com.yas.webhook.repository.WebhookEventNotificationRepository;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@ExtendWith(MockitoExtension.class)
class AbstractWebhookEventNotificationServiceTest {

    @Mock
    private WebhookEventNotificationRepository repository;

    private TestableAbstractService service;

    @BeforeEach
    void setUp() {
        service = new TestableAbstractService(repository);
    }

    @Test
    void persistNotification_shouldSaveWithNotifyingStatus() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode payload = mapper.createObjectNode();
        payload.put("name", "test");

        when(repository.save(any(WebhookEventNotification.class)))
            .thenAnswer(invocation -> {
                WebhookEventNotification n = invocation.getArgument(0);
                n.setId(42L);
                return n;
            });

        Long notificationId = service.callPersistNotification(1L, payload);

        assertEquals(42L, notificationId);

        ArgumentCaptor<WebhookEventNotification> captor = ArgumentCaptor.forClass(WebhookEventNotification.class);
        verify(repository).save(captor.capture());

        WebhookEventNotification saved = captor.getValue();
        assertEquals(1L, saved.getWebhookEventId());
        assertEquals(payload.toString(), saved.getPayload());
        assertEquals(NotificationStatus.NOTIFYING, saved.getNotificationStatus());
        assertNotNull(saved.getCreatedOn());
    }

    @Test
    void createNotificationDto_shouldMapWebhookEventFields() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode payload = mapper.createObjectNode();
        payload.put("data", "value");

        Webhook webhook = new Webhook();
        webhook.setPayloadUrl("https://example.com/hook");
        webhook.setSecret("top-secret");

        WebhookEvent webhookEvent = new WebhookEvent();
        webhookEvent.setId(7L);
        webhookEvent.setWebhook(webhook);

        WebhookEventNotificationDto dto = service.callCreateNotificationDto(webhookEvent, payload, 99L);

        assertEquals(99L, dto.getNotificationId());
        assertEquals("https://example.com/hook", dto.getUrl());
        assertEquals("top-secret", dto.getSecret());
        assertEquals(payload, dto.getPayload());
    }

    @Test
    void createNotificationDto_shouldHandleNullSecret() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode payload = mapper.createObjectNode();

        Webhook webhook = new Webhook();
        webhook.setPayloadUrl("https://example.com/hook");
        webhook.setSecret(null);

        WebhookEvent webhookEvent = new WebhookEvent();
        webhookEvent.setId(1L);
        webhookEvent.setWebhook(webhook);

        WebhookEventNotificationDto dto = service.callCreateNotificationDto(webhookEvent, payload, 1L);

        assertNull(dto.getSecret());
        assertEquals("https://example.com/hook", dto.getUrl());
    }

    @Test
    void createNotificationDto_shouldUseCorrectNotificationId() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode payload = mapper.createObjectNode();

        Webhook webhook = new Webhook();
        webhook.setPayloadUrl("https://example.com");

        WebhookEvent webhookEvent = new WebhookEvent();
        webhookEvent.setId(5L);
        webhookEvent.setWebhook(webhook);

        WebhookEventNotificationDto dto = service.callCreateNotificationDto(webhookEvent, payload, 123L);

        assertEquals(123L, dto.getNotificationId());
    }

    @Test
    void getWebhookEventNotificationRepository_shouldReturnRepository() {
        assertSame(repository, service.getWebhookEventNotificationRepository());
    }

    /**
     * Concrete test subclass exposing protected methods for testing.
     */
    static class TestableAbstractService extends AbstractWebhookEventNotificationService {
        private final WebhookEventNotificationRepository repo;

        TestableAbstractService(WebhookEventNotificationRepository repo) {
            this.repo = repo;
        }

        @Override
        protected WebhookEventNotificationRepository getWebhookEventNotificationRepository() {
            return repo;
        }

        public Long callPersistNotification(Long webhookEventId, JsonNode payload) {
            return persistNotification(webhookEventId, payload);
        }

        public WebhookEventNotificationDto callCreateNotificationDto(
                WebhookEvent webhookEvent, JsonNode payload, Long notificationId) {
            return createNotificationDto(webhookEvent, payload, notificationId);
        }
    }
}
