package com.yas.media.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.media.model.Media;
import com.yas.media.viewmodel.MediaVm;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class MediaVmMapperTest {

    private MediaVmMapper mapper = Mappers.getMapper(MediaVmMapper.class);

    @Test
    void toVm_whenValidMedia_thenReturnMediaVm() {
        Media media = new Media();
        media.setId(1L);
        media.setCaption("caption");
        media.setFileName("file.png");
        media.setMediaType("image/png");

        MediaVm vm = mapper.toVm(media);

        assertNotNull(vm);
        assertEquals(media.getId(), vm.getId());
        assertEquals(media.getCaption(), vm.getCaption());
        assertEquals(media.getFileName(), vm.getFileName());
        assertEquals(media.getMediaType(), vm.getMediaType());
    }

    @Test
    void toModel_whenValidVm_thenReturnMedia() {
        MediaVm vm = new MediaVm(1L, "caption", "file.png", "image/png", "url");

        Media media = mapper.toModel(vm);

        assertNotNull(media);
        assertEquals(vm.getId(), media.getId());
        assertEquals(vm.getCaption(), media.getCaption());
        assertEquals(vm.getFileName(), media.getFileName());
        assertEquals(vm.getMediaType(), media.getMediaType());
    }
}