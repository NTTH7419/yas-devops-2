package com.yas.webhook.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.yas.webhook.model.Event;
import com.yas.webhook.model.enums.EventName;
import com.yas.webhook.model.mapper.EventMapper;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.repository.EventRepository;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    EventRepository eventRepository;
    @Mock
    EventMapper eventMapper;

    @InjectMocks
    EventService eventService;

    @Test
    void findAllEvents_shouldReturnEventsSortedByIdDesc() {
        Event event1 = new Event();
        event1.setId(1L);
        event1.setName(EventName.ON_PRODUCT_UPDATED);

        Event event2 = new Event();
        event2.setId(2L);
        event2.setName(EventName.ON_ORDER_CREATED);

        EventVm vm1 = EventVm.builder().id(1L).name(EventName.ON_PRODUCT_UPDATED).build();
        EventVm vm2 = EventVm.builder().id(2L).name(EventName.ON_ORDER_CREATED).build();

        when(eventRepository.findAll(Sort.by(Sort.Direction.DESC, "id")))
            .thenReturn(List.of(event2, event1));
        when(eventMapper.toEventVm(event2)).thenReturn(vm2);
        when(eventMapper.toEventVm(event1)).thenReturn(vm1);

        List<EventVm> result = eventService.findAllEvents();

        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals(1L, result.get(1).getId());
        verify(eventMapper, times(2)).toEventVm(any(Event.class));
    }

    @Test
    void findAllEvents_shouldReturnEmptyListWhenNoEvents() {
        when(eventRepository.findAll(Sort.by(Sort.Direction.DESC, "id")))
            .thenReturn(List.of());

        List<EventVm> result = eventService.findAllEvents();

        assertTrue(result.isEmpty());
        verify(eventMapper, never()).toEventVm(any());
    }

    @Test
    void findAllEvents_shouldMapAllEvents() {
        Event event = new Event();
        event.setId(3L);
        event.setName(EventName.ON_ORDER_STATUS_UPDATED);
        EventVm vm = EventVm.builder().id(3L).name(EventName.ON_ORDER_STATUS_UPDATED).build();

        when(eventRepository.findAll(Sort.by(Sort.Direction.DESC, "id")))
            .thenReturn(List.of(event));
        when(eventMapper.toEventVm(event)).thenReturn(vm);

        List<EventVm> result = eventService.findAllEvents();

        assertEquals(1, result.size());
        assertEquals(EventName.ON_ORDER_STATUS_UPDATED, result.get(0).getName());
    }

    @Test
    void test_findAllEvents_empty_shouldReturnEmptyList() {
        when(eventRepository.findAll(Sort.by(Sort.Direction.DESC, "id")))
            .thenReturn(Collections.emptyList());

        List<EventVm> result = eventService.findAllEvents();

        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
