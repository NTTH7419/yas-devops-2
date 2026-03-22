package com.yas.webhook.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.webhook.config.constants.MessageCode;
import com.yas.webhook.integration.api.WebhookApi;
import com.yas.webhook.model.Event;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.model.enums.NotificationStatus;
import com.yas.webhook.model.mapper.WebhookMapper;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import com.yas.webhook.repository.EventRepository;
import com.yas.webhook.repository.WebhookEventNotificationRepository;
import com.yas.webhook.repository.WebhookEventRepository;
import com.yas.webhook.repository.WebhookRepository;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@ExtendWith(MockitoExtension.class)
class WebhookServiceTest {

    @Mock
    WebhookRepository webhookRepository;
    @Mock
    EventRepository eventRepository;
    @Mock
    WebhookEventRepository webhookEventRepository;
    @Mock
    WebhookEventNotificationRepository webhookEventNotificationRepository;
    @Mock
    WebhookMapper webhookMapper;
    @Mock
    WebhookApi webHookApi;

    @InjectMocks
    WebhookService webhookService;

    // --- getPageableWebhooks ---

    @Test
    void getPageableWebhooks_shouldReturnPaginatedWebhooks() {
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        Page<Webhook> page = new PageImpl<>(List.of(webhook), PageRequest.of(0, 10), 1);
        WebhookListGetVm expectedVm = new WebhookListGetVm();
        expectedVm.setTotalElements(1);

        when(webhookRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(webhookMapper.toWebhookListGetVm(any(), anyInt(), anyInt())).thenReturn(expectedVm);

        WebhookListGetVm result = webhookService.getPageableWebhooks(0, 10);

        assertNotNull(result);
        assertEquals(expectedVm, result);
        verify(webhookRepository).findAll(any(PageRequest.class));
        verify(webhookMapper).toWebhookListGetVm(any(), anyInt(), anyInt());
    }

    @Test
    void getPageableWebhooks_shouldSortByIdDesc() {
        Page<Webhook> emptyPage = new PageImpl<>(List.of());
        when(webhookRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);
        when(webhookMapper.toWebhookListGetVm(any(), anyInt(), anyInt())).thenReturn(new WebhookListGetVm());

        webhookService.getPageableWebhooks(0, 10);

        verify(webhookRepository).findAll(
                argThat((PageRequest pr) -> pr.getSort().getOrderFor("id").getDirection() == Sort.Direction.DESC));
    }

    // --- findAllWebhooks ---

    @Test
    void findAllWebhooks_shouldReturnAllWebhooksAsVm() {
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        WebhookVm vm = new WebhookVm();
        vm.setId(1L);

        when(webhookRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))).thenReturn(List.of(webhook));
        when(webhookMapper.toWebhookVm(webhook)).thenReturn(vm);

        List<WebhookVm> result = webhookService.findAllWebhooks();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(webhookMapper).toWebhookVm(webhook);
    }

    @Test
    void findAllWebhooks_shouldReturnEmptyListWhenNoWebhooks() {
        when(webhookRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))).thenReturn(List.of());

        List<WebhookVm> result = webhookService.findAllWebhooks();

        assertTrue(result.isEmpty());
    }

    // --- findById ---

    @Test
    void findById_shouldReturnWebhookDetailVm() {
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        WebhookDetailVm expected = new WebhookDetailVm();
        expected.setId(1L);

        when(webhookRepository.findById(1L)).thenReturn(Optional.of(webhook));
        when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(expected);

        WebhookDetailVm result = webhookService.findById(1L);

        assertEquals(expected, result);
    }

    @Test
    void findById_shouldThrowNotFoundExceptionWhenNotFound() {
        when(webhookRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> webhookService.findById(999L));

        assertTrue(exception.getMessage().contains(MessageCode.WEBHOOK_NOT_FOUND));
    }

    // --- create ---

    @Test
    void create_shouldCreateWebhookWithoutEvents() {
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setPayloadUrl("https://example.com/webhook");
        postVm.setSecret("secret");
        postVm.setIsActive(true);

        Webhook created = new Webhook();
        created.setId(1L);
        WebhookDetailVm expected = new WebhookDetailVm();
        expected.setId(1L);

        when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(created);
        when(webhookRepository.save(created)).thenReturn(created);
        when(webhookMapper.toWebhookDetailVm(created)).thenReturn(expected);

        WebhookDetailVm result = webhookService.create(postVm);

        assertEquals(expected, result);
        verify(webhookRepository).save(created);
        verify(webhookEventRepository, never()).saveAll(anyList());
    }

    @Test
    void create_shouldCreateWebhookWithEvents() {
        EventVm eventVm = EventVm.builder().id(10L).build();
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setPayloadUrl("https://example.com/webhook");
        postVm.setSecret("secret");
        postVm.setIsActive(true);
        postVm.setEvents(List.of(eventVm));

        Webhook created = new Webhook();
        created.setId(1L);
        WebhookDetailVm expected = new WebhookDetailVm();
        expected.setId(1L);
        WebhookEvent savedEvent = new WebhookEvent();
        savedEvent.setId(100L);

        Event event = new Event();
        event.setId(10L);

        when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(created);
        when(webhookRepository.save(created)).thenReturn(created);
        when(webhookEventRepository.saveAll(anyList())).thenReturn(List.of(savedEvent));
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(webhookMapper.toWebhookDetailVm(created)).thenReturn(expected);

        WebhookDetailVm result = webhookService.create(postVm);

        assertEquals(expected, result);
        verify(webhookEventRepository).saveAll(anyList());
    }

    @Test
    void create_shouldThrowNotFoundExceptionWhenEventNotFound() {
        EventVm eventVm = EventVm.builder().id(999L).build();
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setPayloadUrl("https://example.com/webhook");
        postVm.setEvents(List.of(eventVm));

        Webhook created = new Webhook();
        created.setId(1L);

        when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(created);
        when(webhookRepository.save(created)).thenReturn(created);
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> webhookService.create(postVm));

        assertTrue(exception.getMessage().contains(MessageCode.EVENT_NOT_FOUND));
    }

    // --- update ---

    @Test
    void update_shouldUpdateWebhookWithEvents() {
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setPayloadUrl("https://updated.com/webhook");
        postVm.setSecret("new-secret");
        postVm.setIsActive(false);
        postVm.setEvents(List.of(EventVm.builder().id(5L).build()));

        Webhook existing = new Webhook();
        existing.setId(1L);
        WebhookEvent oldEvent = new WebhookEvent();
        oldEvent.setId(1L);
        existing.setWebhookEvents(List.of(oldEvent));

        Event event = new Event();
        event.setId(5L);

        when(webhookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(webhookMapper.toUpdatedWebhook(existing, postVm)).thenReturn(existing);
        when(webhookRepository.save(existing)).thenReturn(existing);
        when(webhookEventRepository.saveAll(anyList())).thenReturn(List.of());
        when(eventRepository.findById(5L)).thenReturn(Optional.of(event));

        webhookService.update(postVm, 1L);

        verify(webhookMapper).toUpdatedWebhook(existing, postVm);
        verify(webhookRepository).save(existing);
        verify(webhookEventRepository).deleteAll(anyList());
        verify(webhookEventRepository).saveAll(anyList());
    }

    @Test
    void update_shouldUpdateWebhookWithoutEvents() {
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setPayloadUrl("https://updated.com/webhook");
        postVm.setIsActive(false);

        Webhook existing = new Webhook();
        existing.setId(1L);
        existing.setWebhookEvents(List.of());

        when(webhookRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(webhookMapper.toUpdatedWebhook(existing, postVm)).thenReturn(existing);
        when(webhookRepository.save(existing)).thenReturn(existing);

        webhookService.update(postVm, 1L);

        verify(webhookRepository).save(existing);
        verify(webhookEventRepository, never()).saveAll(anyList());
    }

    @Test
    void update_shouldThrowNotFoundExceptionWhenWebhookNotFound() {
        WebhookPostVm postVm = new WebhookPostVm();

        when(webhookRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> webhookService.update(postVm, 999L));

        assertTrue(exception.getMessage().contains(MessageCode.WEBHOOK_NOT_FOUND));
    }

    // --- delete ---

    @Test
    void delete_shouldDeleteWebhookAndEvents() {
        when(webhookRepository.existsById(1L)).thenReturn(true);
        doNothing().when(webhookEventRepository).deleteByWebhookId(1L);
        doNothing().when(webhookRepository).deleteById(1L);

        assertDoesNotThrow(() -> webhookService.delete(1L));

        verify(webhookEventRepository).deleteByWebhookId(1L);
        verify(webhookRepository).deleteById(1L);
    }

    @Test
    void delete_shouldThrowNotFoundExceptionWhenWebhookNotFound() {
        when(webhookRepository.existsById(999L)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> webhookService.delete(999L));

        assertTrue(exception.getMessage().contains(MessageCode.WEBHOOK_NOT_FOUND));
    }

    // --- notifyToWebhook ---

    @Test
    void notifyToWebhook_shouldCallApiAndUpdateStatus() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("key", "value");

        WebhookEventNotificationDto dto = WebhookEventNotificationDto.builder()
                .notificationId(1L)
                .url("https://example.com/hook")
                .secret("secret")
                .payload(payload)
                .build();

        WebhookEventNotification notification = new WebhookEventNotification();
        notification.setNotificationStatus(NotificationStatus.NOTIFYING);

        when(webhookEventNotificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        webhookService.notifyToWebhook(dto);

        verify(webHookApi).notify("https://example.com/hook", "secret", payload);
        assertEquals(NotificationStatus.NOTIFIED, notification.getNotificationStatus());
        verify(webhookEventNotificationRepository).save(notification);
    }

    @Test
    void notifyToWebhook_shouldThrowWhenNotificationNotFound() {
        WebhookEventNotificationDto dto = WebhookEventNotificationDto.builder()
                .notificationId(999L)
                .url("https://example.com/hook")
                .secret("secret")
                .build();

        when(webhookEventNotificationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> webhookService.notifyToWebhook(dto));
    }

    @Test
    void test_getPageableWebhooks_withResults_shouldReturnPage() {
        int pageNo = 0;
        int pageSize = 10;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Webhook webhook = new Webhook();
        Page<Webhook> webhooks = new PageImpl<>(List.of(webhook));
        WebhookListGetVm expectedVm = WebhookListGetVm.builder()
                .webhooks(Collections.emptyList())
                .pageNo(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .isLast(true)
                .build();

        when(webhookRepository.findAll(pageRequest)).thenReturn(webhooks);
        when(webhookMapper.toWebhookListGetVm(webhooks, pageNo, pageSize)).thenReturn(expectedVm);

        WebhookListGetVm result = webhookService.getPageableWebhooks(pageNo, pageSize);

        assertNotNull(result);
        assertEquals(expectedVm, result);
    }

    @Test
    void test_findAllWebhooks_empty_shouldReturnEmptyList() {
        when(webhookRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))).thenReturn(Collections.emptyList());

        List<WebhookVm> result = webhookService.findAllWebhooks();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void test_create_withoutEvents_shouldReturnCreatedVm() {
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setEvents(null);

        Webhook webhook = new Webhook();
        webhook.setId(1L);
        WebhookDetailVm detailVm = new WebhookDetailVm();
        detailVm.setId(1L);

        when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(webhook);
        when(webhookRepository.save(webhook)).thenReturn(webhook);
        when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(detailVm);

        WebhookDetailVm result = webhookService.create(postVm);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(webhookRepository).save(webhook);
        verify(webhookEventRepository, never()).saveAll(any());
    }

    @Test
    void test_create_whenEventNotFound_shouldThrowException() {
        com.yas.webhook.model.viewmodel.webhook.EventVm eventVm = new com.yas.webhook.model.viewmodel.webhook.EventVm();
        eventVm.setId(1L);
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setEvents(List.of(eventVm));

        Webhook webhook = new Webhook();
        webhook.setId(1L);

        when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(webhook);
        when(webhookRepository.save(webhook)).thenReturn(webhook);
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> webhookService.create(postVm));
    }

    @Test
    void test_update_withEvents_shouldUpdate() {
        Long id = 1L;
        com.yas.webhook.model.viewmodel.webhook.EventVm eventVm = new com.yas.webhook.model.viewmodel.webhook.EventVm();
        eventVm.setId(2L);
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setEvents(List.of(eventVm));

        Webhook existedWebhook = new Webhook();
        existedWebhook.setWebhookEvents(Collections.emptyList());
        Webhook updatedWebhook = new Webhook();

        when(webhookRepository.findById(id)).thenReturn(Optional.of(existedWebhook));
        when(webhookMapper.toUpdatedWebhook(existedWebhook, postVm)).thenReturn(updatedWebhook);
        when(eventRepository.findById(2L)).thenReturn(Optional.of(new com.yas.webhook.model.Event()));

        webhookService.update(postVm, id);

        verify(webhookRepository).save(updatedWebhook);
        verify(webhookEventRepository).deleteAll(any());
        verify(webhookEventRepository).saveAll(any());
    }

    @Test
    void test_update_whenEventNotFound_shouldThrowException() {
        Long id = 1L;
        com.yas.webhook.model.viewmodel.webhook.EventVm eventVm = new com.yas.webhook.model.viewmodel.webhook.EventVm();
        eventVm.setId(2L);
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setEvents(List.of(eventVm));

        Webhook existedWebhook = new Webhook();
        existedWebhook.setWebhookEvents(Collections.emptyList());
        Webhook updatedWebhook = new Webhook();

        when(webhookRepository.findById(id)).thenReturn(Optional.of(existedWebhook));
        when(webhookMapper.toUpdatedWebhook(existedWebhook, postVm)).thenReturn(updatedWebhook);
        when(eventRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> webhookService.update(postVm, id));
    }

    @Test
    void test_notifyToWebhook_whenNotificationNotFound_shouldThrowException() {
        WebhookEventNotificationDto notificationDto = WebhookEventNotificationDto
                .builder()
                .notificationId(1L)
                .url("http://example.com")
                .secret("secret")
                .build();

        when(webhookEventNotificationRepository.findById(notificationDto.getNotificationId()))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> webhookService.notifyToWebhook(notificationDto));
    }
}
