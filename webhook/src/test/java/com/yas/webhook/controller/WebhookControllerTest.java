package com.yas.webhook.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import com.yas.webhook.service.WebhookService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = WebhookController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private WebhookService webhookService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    private WebhookListGetVm makeListVm(int totalElements) {
        WebhookListGetVm vm = new WebhookListGetVm();
        vm.setWebhooks(List.of(new WebhookVm()));
        vm.setPageNo(0);
        vm.setPageSize(10);
        vm.setTotalElements(totalElements);
        vm.setTotalPages(1);
        vm.setLast(true);
        return vm;
    }

    @Test
    void getPageableWebhooks_shouldReturn200WithPage() throws Exception {
        WebhookListGetVm vm = makeListVm(1);
        when(webhookService.getPageableWebhooks(0, 10)).thenReturn(vm);

        mockMvc.perform(get("/backoffice/webhooks/paging")
                .param("pageNo", "0")
                .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pageNo").value(0))
            .andExpect(jsonPath("$.pageSize").value(10))
            .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getPageableWebhooks_shouldUseDefaultPaginationWhenNotProvided() throws Exception {
        WebhookListGetVm vm = makeListVm(0);
        when(webhookService.getPageableWebhooks(0, 10)).thenReturn(vm);

        mockMvc.perform(get("/backoffice/webhooks/paging"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pageNo").value(0))
            .andExpect(jsonPath("$.pageSize").value(10));
    }

    @Test
    void listWebhooks_shouldReturn200WithAllWebhooks() throws Exception {
        WebhookVm vm = new WebhookVm();
        vm.setId(1L);
        vm.setPayloadUrl("https://example.com");
        vm.setIsActive(true);
        when(webhookService.findAllWebhooks()).thenReturn(List.of(vm));

        mockMvc.perform(get("/backoffice/webhooks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].payloadUrl").value("https://example.com"));
    }

    @Test
    void listWebhooks_shouldReturnEmptyListWhenNoWebhooks() throws Exception {
        when(webhookService.findAllWebhooks()).thenReturn(List.of());

        mockMvc.perform(get("/backoffice/webhooks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getWebhook_shouldReturn200WhenFound() throws Exception {
        WebhookDetailVm vm = new WebhookDetailVm();
        vm.setId(1L);
        vm.setPayloadUrl("https://example.com");
        vm.setIsActive(true);
        when(webhookService.findById(1L)).thenReturn(vm);

        mockMvc.perform(get("/backoffice/webhooks/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.payloadUrl").value("https://example.com"));
    }

    @Test
    void getWebhook_shouldReturn404WhenNotFound() throws Exception {
        when(webhookService.findById(999L))
            .thenThrow(new NotFoundException("WEBHOOK_NOT_FOUND", 999L));

        mockMvc.perform(get("/backoffice/webhooks/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void createWebhook_shouldReturn201Created() throws Exception {
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setPayloadUrl("https://example.com");
        postVm.setSecret("secret");
        postVm.setIsActive(true);

        WebhookDetailVm created = new WebhookDetailVm();
        created.setId(1L);
        created.setPayloadUrl("https://example.com");
        created.setIsActive(true);

        when(webhookService.create(any(WebhookPostVm.class))).thenReturn(created);

        mockMvc.perform(post("/backoffice/webhooks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.payloadUrl").value("https://example.com"));
    }

    @Test
    void updateWebhook_shouldReturn204NoContent() throws Exception {
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setPayloadUrl("https://updated.com");
        postVm.setIsActive(false);

        doNothing().when(webhookService).update(any(WebhookPostVm.class), eq(1L));

        mockMvc.perform(put("/backoffice/webhooks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
            .andExpect(status().isNoContent());

        verify(webhookService).update(any(WebhookPostVm.class), eq(1L));
    }

    @Test
    void updateWebhook_shouldReturn404WhenNotFound() throws Exception {
        WebhookPostVm postVm = new WebhookPostVm();
        postVm.setPayloadUrl("https://updated.com");

        doThrow(new NotFoundException("WEBHOOK_NOT_FOUND", 999L))
            .when(webhookService).update(any(WebhookPostVm.class), eq(999L));

        mockMvc.perform(put("/backoffice/webhooks/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postVm)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteWebhook_shouldReturn204NoContent() throws Exception {
        doNothing().when(webhookService).delete(1L);

        mockMvc.perform(delete("/backoffice/webhooks/1"))
            .andExpect(status().isNoContent());

        verify(webhookService).delete(1L);
    }

    @Test
    void deleteWebhook_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new NotFoundException("WEBHOOK_NOT_FOUND", 999L))
            .when(webhookService).delete(999L);

        mockMvc.perform(delete("/backoffice/webhooks/999"))
            .andExpect(status().isNotFound());
    }
}
