package com.yas.webhook.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.webhook.model.enums.EventName;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.service.EventService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EventController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Test
    void listEvents_shouldReturn200WithEvents() throws Exception {
        EventVm eventVm = EventVm.builder()
            .id(1L)
            .name(EventName.ON_ORDER_CREATED)
            .build();
        when(eventService.findAllEvents()).thenReturn(List.of(eventVm));

        mockMvc.perform(get("/backoffice/events"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("ON_ORDER_CREATED"));

        verify(eventService).findAllEvents();
    }

    @Test
    void listEvents_shouldReturnEmptyListWhenNoEvents() throws Exception {
        when(eventService.findAllEvents()).thenReturn(List.of());

        mockMvc.perform(get("/backoffice/events"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void listEvents_shouldReturnAllEventTypes() throws Exception {
        EventVm event1 = EventVm.builder().id(1L).name(EventName.ON_PRODUCT_UPDATED).build();
        EventVm event2 = EventVm.builder().id(2L).name(EventName.ON_ORDER_CREATED).build();
        EventVm event3 = EventVm.builder().id(3L).name(EventName.ON_ORDER_STATUS_UPDATED).build();
        when(eventService.findAllEvents()).thenReturn(List.of(event1, event2, event3));

        mockMvc.perform(get("/backoffice/events"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
    }
}
