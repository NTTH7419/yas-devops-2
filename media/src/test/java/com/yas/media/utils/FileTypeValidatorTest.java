package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class FileTypeValidatorTest {

    private FileTypeValidator validator;
    private ConstraintValidatorContext context;
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        validator = new FileTypeValidator();
        context = mock(ConstraintValidatorContext.class);
        builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);

        ValidFileType annotation = mock(ValidFileType.class);
        given(annotation.allowedTypes()).willReturn(new String[] { "image/jpeg", "image/png" });
        given(annotation.message()).willReturn("Invalid file");

        validator.initialize(annotation);

        given(context.buildConstraintViolationWithTemplate(anyString())).willReturn(builder);
    }

    @Test
    void isValid_NullFile_ReturnsFalse() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    void isValid_NullContentType_ReturnsFalse() {
        MultipartFile file = mock(MultipartFile.class);
        given(file.getContentType()).willReturn(null);
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void isValid_NotAllowedType_ReturnsFalse() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "content".getBytes());
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void isValid_AllowedTypeButInvalidImage_ReturnsFalse() {
        // Mock an invalid image content (not a real image formatting)
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", "fake image content".getBytes());
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void isValid_ValidImage_ReturnsTrue() throws Exception {
        // We need an actual minimal valid PNG for ImageIO.read to not return null.
        // A simple 1x1 transparent PNG:
        String base64Png = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=";
        byte[] validPngBytes = java.util.Base64.getDecoder().decode(base64Png);

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", validPngBytes);
        assertTrue(validator.isValid(file, context));
    }
}
