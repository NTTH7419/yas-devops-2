package com.yas.webhook.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import java.util.NoSuchElementException;
import com.yas.webhook.integration.api.WebhookApi;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEventNotification;
import com.yas.webhook.model.dto.WebhookEventNotificationDto;
import com.yas.webhook.model.mapper.WebhookMapper;
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

    @Test
    void test_getPageableWebhooks_shouldReturnPage() {
        int pageNo = 0;
        int pageSize = 10;
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "id"));
        Page<Webhook> webhooks = new PageImpl<>(Collections.emptyList());
        WebhookListGetVm expectedVm = WebhookListGetVm.builder()
            .webhooks(Collections.emptyList())
            .pageNo(0)
            .pageSize(0)
            .totalElements(0)
            .totalPages(0)
            .isLast(false)
            .build();

        when(webhookRepository.findAll(pageRequest)).thenReturn(webhooks);
        when(webhookMapper.toWebhookListGetVm(webhooks, pageNo, pageSize)).thenReturn(expectedVm);

        WebhookListGetVm result = webhookService.getPageableWebhooks(pageNo, pageSize);

        assertNotNull(result);
        assertEquals(expectedVm, result);
    }

    @Test
    void test_findAllWebhooks_shouldReturnList() {
        List<Webhook> webhooks = List.of(new Webhook());
        WebhookVm vm = new WebhookVm();

        when(webhookRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))).thenReturn(webhooks);
        when(webhookMapper.toWebhookVm(any())).thenReturn(vm);

        List<WebhookVm> result = webhookService.findAllWebhooks();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void test_findById_whenFound_shouldReturnVm() {
        Long id = 1L;
        Webhook webhook = new Webhook();
        WebhookDetailVm expectedVm = new WebhookDetailVm();
        expectedVm.setId(id);

        when(webhookRepository.findById(id)).thenReturn(Optional.of(webhook));
        when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(expectedVm);

        WebhookDetailVm result = webhookService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void test_findById_whenNotFound_shouldThrowException() {
        Long id = 1L;
        when(webhookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> webhookService.findById(id));
    }

    @Test
    void test_create_withEvents_shouldReturnCreatedVm() {
        com.yas.webhook.model.viewmodel.webhook.EventVm eventVm = new com.yas.webhook.model.viewmodel.webhook.EventVm();
        eventVm.setId(1L);
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setEvents(List.of(eventVm));
        
        Webhook webhook = new Webhook();
        webhook.setId(1L);
        WebhookDetailVm detailVm = new WebhookDetailVm();
        detailVm.setId(1L);

        when(webhookMapper.toCreatedWebhook(postVm)).thenReturn(webhook);
        when(webhookRepository.save(webhook)).thenReturn(webhook);
        when(webhookMapper.toWebhookDetailVm(webhook)).thenReturn(detailVm);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(new com.yas.webhook.model.Event()));

        WebhookDetailVm result = webhookService.create(postVm);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(webhookRepository).save(webhook);
        verify(webhookEventRepository).saveAll(any());
    }

    @Test
    void test_update_whenFound_shouldUpdate() {
        Long id = 1L;
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setEvents(Collections.emptyList());
        Webhook existedWebhook = new Webhook();
        existedWebhook.setWebhookEvents(Collections.emptyList());
        Webhook updatedWebhook = new Webhook();

        when(webhookRepository.findById(id)).thenReturn(Optional.of(existedWebhook));
        when(webhookMapper.toUpdatedWebhook(existedWebhook, postVm)).thenReturn(updatedWebhook);

        webhookService.update(postVm, id);

        verify(webhookRepository).save(updatedWebhook);
        verify(webhookEventRepository).deleteAll(any());
    }

    @Test
    void test_update_whenNotFound_shouldThrowException() {
        Long id = 1L;
        WebhookPostVm postVm = new WebhookPostVm();
        when(webhookRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> webhookService.update(postVm, id));
    }

    @Test
    void test_delete_whenFound_shouldDelete() {
        Long id = 1L;
        when(webhookRepository.existsById(id)).thenReturn(true);

        webhookService.delete(id);

        verify(webhookEventRepository).deleteByWebhookId(id);
        verify(webhookRepository).deleteById(id);
    }

    @Test
    void test_delete_whenNotFound_shouldThrowException() {
        Long id = 1L;
        when(webhookRepository.existsById(id)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> webhookService.delete(id));
    }

    @Test
    void test_notifyToWebhook_ShouldNotException() {

        WebhookEventNotificationDto notificationDto = WebhookEventNotificationDto
            .builder()
            .notificationId(1L)
            .url("http://example.com")
            .secret("secret")
            .build();

        WebhookEventNotification notification = new WebhookEventNotification();
        when(webhookEventNotificationRepository.findById(notificationDto.getNotificationId()))
            .thenReturn(Optional.of(notification));

        webhookService.notifyToWebhook(notificationDto);

        verify(webhookEventNotificationRepository).save(notification);
        verify(webHookApi).notify(notificationDto.getUrl(), notificationDto.getSecret(), notificationDto.getPayload());
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
