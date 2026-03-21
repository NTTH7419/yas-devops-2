package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class FileTypeValidatorTest {

    private FileTypeValidator validator;
    private final String[] allowedTypes = {"image/jpeg", "image/png"};
    private final String message = "Invalid file type";

    @BeforeEach
    void setUp() {
        validator = new FileTypeValidator();
        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.allowedTypes()).thenReturn(allowedTypes);
        when(annotation.message()).thenReturn(message);
        validator.initialize(annotation);
    }

    @Test
    void isValid_whenFileIsValid_thenReturnTrue() throws IOException {
        byte[] content = getClass().getResourceAsStream("/test.png").readAllBytes();
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", content);
        assertTrue(validator.isValid(file, mock(ConstraintValidatorContext.class)));
    }

    @Test
    void isValid_whenFileIsNull_thenReturnFalse() {
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(message)).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        assertFalse(validator.isValid(null, context));
    }

    @Test
    void isValid_whenContentTypeIsNull_thenReturnFalse() {
        MultipartFile file = new MockMultipartFile("file", "test.png", null, new byte[0]);
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(message)).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        assertFalse(validator.isValid(file, context));
    }

    @Test
    void isValid_whenTypeNotAllowed_thenReturnFalse() {
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", new byte[0]);
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);
        ConstraintValidatorContext.ConstraintViolationBuilder builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate(message)).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);

        assertFalse(validator.isValid(file, context));
    }

    @Test
    void isValid_whenNotAnImage_thenReturnFalse() {
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "not an image".getBytes());
        assertFalse(validator.isValid(file, mock(ConstraintValidatorContext.class)));
    }
}
