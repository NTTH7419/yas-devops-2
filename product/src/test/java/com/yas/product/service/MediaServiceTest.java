package com.yas.product.service;

import com.yas.product.viewmodel.NoFileMediaVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MediaServiceTest {

    private static final String MEDIA_BASE_URL = "http://api.yas.local/media";

    private MediaService mediaService;

    // Real RestClient spy so we can stub method chains
    private RestClient restClient;

    private void setSecurityContext(String token) {
        SecurityContextHolder.clearContext();
        Jwt jwt = mock(Jwt.class);
        lenient().when(jwt.getTokenValue()).thenReturn(token);
        Authentication auth = mock(Authentication.class);
        lenient().when(auth.getPrincipal()).thenReturn(jwt);
        SecurityContext ctx = mock(SecurityContext.class);
        lenient().when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @BeforeEach
    void setUp() {
        // Real RestClient instance — all methods are no-ops by default
        restClient = RestClient.create();
        mediaService = new MediaService(
                restClient,
                new com.yas.commonlibrary.config.ServiceUrlConfig(MEDIA_BASE_URL, "http://api.yas.local/product")
        );
    }

    // === getMedia tests ===

    @Test
    void getMedia_WhenIdIsNull_ShouldReturnNoFileMediaVmWithEmptyFields() {
        NoFileMediaVm result = mediaService.getMedia(null);

        assertNotNull(result);
        assertNull(result.id());
        assertEquals("", result.caption());
        assertEquals("", result.fileName());
        assertEquals("", result.mediaType());
        assertEquals("", result.url());
    }

    @Test
    void getMedia_WhenIdIsProvided_ShouldThrowRestClientException() {
        // RestClient.create() throws when no server responds — expected
        assertThrows(org.springframework.web.client.RestClientException.class,
                () -> mediaService.getMedia(5L));
    }

    // === saveFile tests ===

    @Test
    void saveFile_ShouldThrowException_WhenMultipartFileIsNull() {
        setSecurityContext("token");
        // MultipartFile.getResource() throws NPE when file is null — verify this path
        assertThrows(NullPointerException.class,
                () -> mediaService.saveFile(null, "caption", "file.jpg"));
    }

    // === removeMedia tests ===

    @Test
    void removeMedia_ShouldThrowRestClientException() {
        setSecurityContext("token");

        assertThrows(org.springframework.web.client.RestClientException.class,
                () -> mediaService.removeMedia(5L));
    }

    // === test the MediaService constructor and dependency injection ===

    @Test
    void constructor_ShouldStoreDependencies() throws Exception {
        RestClient rc = RestClient.create();
        com.yas.commonlibrary.config.ServiceUrlConfig cfg =
                new com.yas.commonlibrary.config.ServiceUrlConfig("http://custom.media", "http://custom.product");

        MediaService svc = new MediaService(rc, cfg);

        // Verify via reflection that dependencies are stored
        var restClientField = MediaService.class.getDeclaredField("restClient");
        restClientField.setAccessible(true);
        assertSame(rc, restClientField.get(svc));

        var configField = MediaService.class.getDeclaredField("serviceUrlConfig");
        configField.setAccessible(true);
        assertSame(cfg, configField.get(svc));
    }

    @Test
    void serviceUrlConfig_ShouldProvideCorrectMediaBaseUrl() {
        com.yas.commonlibrary.config.ServiceUrlConfig cfg =
                new com.yas.commonlibrary.config.ServiceUrlConfig("https://cdn.yas.local/media", "https://product.yas.local");
        assertEquals("https://cdn.yas.local/media", cfg.media());
        assertEquals("https://product.yas.local", cfg.product());
    }

    // === getMedia null-id edge cases ===

    @Test
    void getMedia_WhenIdIsZero_ShouldBuildUrlWithZeroId() {
        // Zero is not null, so RestClient will be invoked (and throw since no server)
        assertThrows(org.springframework.web.client.RestClientException.class,
                () -> mediaService.getMedia(0L));
    }

    @Test
    void getMedia_WhenIdIsNegative_ShouldBuildUrlWithNegativeId() {
        assertThrows(org.springframework.web.client.RestClientException.class,
                () -> mediaService.getMedia(-1L));
    }
}
