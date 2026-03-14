package com.yas.recommendation.service;

import com.yas.recommendation.configuration.RecommendationConfig;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RecommendationConfig config;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        when(config.getApiUrl()).thenReturn("http://api.example.com");
    }

    @Test
    void getProductDetail_shouldReturnProductDetail() {
        long productId = 1L;
        ProductDetailVm expectedProduct = new ProductDetailVm(productId, "Test Product", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(org.springframework.http.ResponseEntity.ok(expectedProduct));

        ProductDetailVm actualProduct = productService.getProductDetail(productId);

        assertEquals(expectedProduct, actualProduct);
    }
}
