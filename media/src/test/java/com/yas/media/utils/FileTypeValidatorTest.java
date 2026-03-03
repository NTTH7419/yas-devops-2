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

    // A valid image test might be tricky without a real image byte array.
    // We can simulate an empty real image or we can test an actual 1x1 png byte
    // array.
    @Test
    void isValid_ValidImage_ReturnsTrue() {
        // Base64 encoded 1x1 transparent PNG:
        // iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVQYV2NgYAAAAAMAAWgmWQ0AAAAASUVORK5CYII=
        byte[] validPng = new byte[] {
                (byte) 137, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82,
                0, 0, 0, 1, 0, 0, 0, 1, 8, 6, 0, 0, 0, 31, 21, 196, 137, 0, 0, 0,
                11, 73, 68, 65, 84, 8, 153, 99, 96, 0, 0, 0, 2, 0, 1, 242, 43, 173,
                144, 0, 0, 0, 0, 73, 69, 78, 68, 174, 66, 96, 130
        };

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.png", "image/png", validPng);
        assertTrue(validator.isValid(file, context));
    }
}
