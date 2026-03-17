package com.yas.media.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.server.resource.autoconfigure.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MediaController.class,
    excludeAutoConfiguration = OAuth2ResourceServerAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MediaService mediaService;

    @Test
    void create_whenValidRequest_thenReturnOk() throws Exception {
        Media media = new Media();
        media.setId(1L);
        media.setCaption("caption");
        media.setFileName("file.png");
        media.setMediaType("image/png");

        when(mediaService.saveMedia(any(MediaPostVm.class))).thenReturn(media);

        byte[] imageBytes;
        try (var is = getClass().getResourceAsStream("/test.png")) {
            imageBytes = is != null ? is.readAllBytes() : new byte[0];
        }

        MockMultipartFile file = new MockMultipartFile("multipartFile", "file.png", "image/png", imageBytes);

        mockMvc.perform(multipart("/medias")
                .file(file)
                .param("caption", "caption"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.fileName").value("file.png"));
    }

    @Test
    void delete_whenValidId_thenReturnNoContent() throws Exception {
        doNothing().when(mediaService).removeMedia(1L);

        mockMvc.perform(delete("/medias/{id}", 1L))
            .andExpect(status().isNoContent());
    }

    @Test
    void get_whenValidId_thenReturnOk() throws Exception {
        MediaVm mediaVm = new MediaVm(1L, "caption", "file.png", "image/png", "url");
        when(mediaService.getMediaById(1L)).thenReturn(mediaVm);

        mockMvc.perform(get("/medias/{id}", 1L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.url").value("url"));
    }

    @Test
    void get_whenNotFound_thenReturnNotFound() throws Exception {
        when(mediaService.getMediaById(1L)).thenReturn(null);

        mockMvc.perform(get("/medias/{id}", 1L))
            .andExpect(status().isNotFound());
    }

    @Test
    void getByIds_whenValidIds_thenReturnOk() throws Exception {
        MediaVm mediaVm = new MediaVm(1L, "caption", "file.png", "image/png", "url");
        when(mediaService.getMediaByIds(any())).thenReturn(List.of(mediaVm));

        mockMvc.perform(get("/medias").param("ids", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getByIds_whenEmptyResults_thenReturnNotFound() throws Exception {
        when(mediaService.getMediaByIds(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/medias").param("ids", "1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getFile_whenValidRequest_thenReturnFile() throws Exception {
        MediaDto mediaDto = MediaDto.builder()
            .content(new ByteArrayInputStream("content".getBytes()))
            .mediaType(MediaType.IMAGE_PNG)
            .build();

        when(mediaService.getFile(anyLong(), anyString())).thenReturn(mediaDto);

        mockMvc.perform(get("/medias/{id}/file/{fileName}", 1L, "file.png"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"file.png\""))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE));
    }
}
