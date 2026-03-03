package com.yas.media.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
import java.io.ByteArrayInputStream;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MediaController.class)
@AutoConfigureMockMvc(addFilters = false)
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaService mediaService;

    private Media media;
    private MediaVm mediaVm;

    @BeforeEach
    void setUp() {
        media = new Media();
        media.setId(1L);
        media.setCaption("test caption");
        media.setFileName("test.png");
        media.setMediaType(MediaType.IMAGE_PNG_VALUE);

        mediaVm = new MediaVm(1L, "test caption", "test.png", MediaType.IMAGE_PNG_VALUE, "/media/test.png");
    }

    @Test
    void create_ValidMedia_ReturnsOk() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "multipartFile",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "test image content".getBytes());

        given(mediaService.saveMedia(any(MediaPostVm.class))).willReturn(media);

        mockMvc.perform(multipart("/medias")
                .file(file)
                .param("caption", "test caption")
                .param("fileNameOverride", "test.png")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.caption").value("test caption"))
                .andExpect(jsonPath("$.fileName").value("test.png"))
                .andExpect(jsonPath("$.mediaType").value(MediaType.IMAGE_PNG_VALUE));
    }

    @Test
    void delete_ValidId_ReturnsNoContent() throws Exception {
        doNothing().when(mediaService).removeMedia(1L);

        mockMvc.perform(delete("/medias/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    void get_ValidId_ReturnsMedia() throws Exception {
        given(mediaService.getMediaById(1L)).willReturn(mediaVm);

        mockMvc.perform(get("/medias/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.caption").value("test caption"));
    }

    @Test
    void get_InvalidId_ReturnsNotFound() throws Exception {
        given(mediaService.getMediaById(2L)).willReturn(null);

        mockMvc.perform(get("/medias/{id}", 2L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByIds_ValidIds_ReturnsMediaList() throws Exception {
        given(mediaService.getMediaByIds(List.of(1L))).willReturn(List.of(mediaVm));

        mockMvc.perform(get("/medias")
                .param("ids", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getByIds_EmptyList_ReturnsNotFound() throws Exception {
        given(mediaService.getMediaByIds(List.of(2L))).willReturn(List.of());

        mockMvc.perform(get("/medias")
                .param("ids", "2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFile_ValidIdAndFileName_ReturnsFile() throws Exception {
        byte[] content = "test content".getBytes();
        MediaDto mediaDto = MediaDto.builder()
                .content(new ByteArrayInputStream(content))
                .mediaType(MediaType.IMAGE_PNG)
                .build();

        given(mediaService.getFile(1L, "test.png")).willReturn(mediaDto);

        mockMvc.perform(get("/medias/{id}/file/{fileName}", 1L, "test.png"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"test.png\""))
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(content));
    }
}
