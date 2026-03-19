package com.yas.product.service;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.model.ProductRelated;
import com.yas.product.model.enumeration.FilterExistInWhSelection;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRelatedRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailVm;
import com.yas.product.viewmodel.product.ProductExportingDetailVm;
import com.yas.product.viewmodel.product.ProductFeatureGetVm;
import com.yas.product.viewmodel.product.ProductInfoVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductQuantityPostVm;
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import com.yas.product.viewmodel.product.ProductSlugGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import com.yas.product.viewmodel.product.ProductsGetVm;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private MediaService mediaService;
    @Mock private BrandRepository brandRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductCategoryRepository productCategoryRepository;
    @Mock private ProductImageRepository productImageRepository;
    @Mock private ProductOptionRepository productOptionRepository;
    @Mock private ProductOptionValueRepository productOptionValueRepository;
    @Mock private ProductOptionCombinationRepository productOptionCombinationRepository;
    @Mock private ProductRelatedRepository productRelatedRepository;

    @InjectMocks
    private ProductService productService;

    // =========================================================
    // Helper: build Product dùng @Builder từ Product.java
    // =========================================================
    private Product buildProduct(Long id, String name, String slug) {
        return Product.builder()
            .id(id)
            .name(name)
            .slug(slug)
            .sku("SKU-" + id)
            .gtin("")
            .price(100.0)
            .weight(1.0)
            .length(10.0)
            .width(5.0)
            .height(3.0)
            .isPublished(true)
            .isAllowedToOrder(true)
            .isFeatured(false)
            .isVisibleIndividually(true)
            .stockTrackingEnabled(false)
            .productCategories(new ArrayList<>())
            .productImages(new ArrayList<>())
            .products(new ArrayList<>())
            .relatedProducts(new ArrayList<>())
            .attributeValues(new ArrayList<>())
            .build();
    }

    // =========================================================
    // getProductById
    // =========================================================
    @Nested
    class GetProductById {

        @Test
        void getProductById_WhenProductExists_ShouldReturnProductDetailVm() {
            Product product = buildProduct(1L, "Product A", "product-a");
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            ProductDetailVm result = productService.getProductById(1L);

            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("Product A");
            assertThat(result.slug()).isEqualTo("product-a");
        }

        @Test
        void getProductById_WhenProductNotFound_ShouldThrowNotFoundException() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(NotFoundException.class);
        }

        @Test
        void getProductById_WhenProductHasThumbnail_ShouldCallMediaService() {
            Product product = buildProduct(1L, "Product A", "product-a");
            product.setThumbnailMediaId(10L);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "http://thumb.url"));

            ProductDetailVm result = productService.getProductById(1L);

            assertThat(result.thumbnailMedia()).isNotNull();
            assertThat(result.thumbnailMedia().url()).isEqualTo("http://thumb.url");
        }

        @Test
        void getProductById_WhenProductHasImages_ShouldReturnImageList() {
            Product product = buildProduct(1L, "Product A", "product-a");
            ProductImage img = ProductImage.builder().imageId(20L).product(product).build();
            product.setProductImages(List.of(img));
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(mediaService.getMedia(20L)).thenReturn(new NoFileMediaVm(20L, "", "", "", "http://image.url"));

            ProductDetailVm result = productService.getProductById(1L);

            assertThat(result.productImageMedias()).hasSize(1);
            assertThat(result.productImageMedias().get(0).url()).isEqualTo("http://image.url");
        }

        @Test
        void getProductById_WhenProductHasCategories_ShouldReturnCategories() {
            Product product = buildProduct(1L, "Product A", "product-a");
            Category cat = new Category();
            cat.setId(5L);
            cat.setName("Electronics");
            ProductCategory pc = ProductCategory.builder()
                .product(product).category(cat).build();
            product.setProductCategories(List.of(pc));
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            ProductDetailVm result = productService.getProductById(1L);

            assertThat(result.categories()).hasSize(1);
            assertThat(result.categories().get(0).getName()).isEqualTo("Electronics");
        }

        @Test
        void getProductById_WhenProductHasBrand_ShouldReturnBrandId() {
            Product product = buildProduct(1L, "Product A", "product-a");
            Brand brand = new Brand();
            brand.setId(7L);
            product.setBrand(brand);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            ProductDetailVm result = productService.getProductById(1L);

            assertThat(result.brandId()).isEqualTo(7L);
        }

        @Test
        void getProductById_WhenNoBrand_ShouldReturnNullBrandId() {
            Product product = buildProduct(1L, "Product A", "product-a");
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            ProductDetailVm result = productService.getProductById(1L);

            assertThat(result.brandId()).isNull();
        }

        @Test
        void getProductById_WhenProductHasParent_ShouldReturnParentId() {
            Product parent = buildProduct(2L, "Parent", "parent");
            Product product = buildProduct(1L, "Variation", "variation");
            product.setParent(parent);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            ProductDetailVm result = productService.getProductById(1L);

            assertThat(result.parentId()).isEqualTo(2L);
        }
    }

    // =========================================================
    // deleteProduct
    // =========================================================
    @Nested
    class DeleteProduct {

        @Test
        void deleteProduct_WhenProductExists_ShouldSetPublishedFalseAndSave() {
            Product product = buildProduct(1L, "Product A", "product-a");
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            productService.deleteProduct(1L);

            assertThat(product.isPublished()).isFalse();
            verify(productRepository).save(product);
        }

        @Test
        void deleteProduct_WhenProductNotFound_ShouldThrowNotFoundException() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.deleteProduct(99L))
                .isInstanceOf(NotFoundException.class);
        }

        @Test
        void deleteProduct_WhenVariationHasCombinations_ShouldDeleteCombinations() {
            Product parent = buildProduct(2L, "Parent", "parent");
            Product product = buildProduct(1L, "Variation", "variation");
            product.setParent(parent);
            ProductOptionCombination combination = new ProductOptionCombination();

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productOptionCombinationRepository.findAllByProduct(product))
                .thenReturn(List.of(combination));

            productService.deleteProduct(1L);

            verify(productOptionCombinationRepository).deleteAll(List.of(combination));
        }

        @Test
        void deleteProduct_WhenVariationHasNoCombinations_ShouldNotCallDeleteAll() {
            Product parent = buildProduct(2L, "Parent", "parent");
            Product product = buildProduct(1L, "Variation", "variation");
            product.setParent(parent);

            when(productRepository.findById(1L)).thenReturn(Optional.of(product));
            when(productOptionCombinationRepository.findAllByProduct(product))
                .thenReturn(Collections.emptyList());

            productService.deleteProduct(1L);

            verify(productOptionCombinationRepository, never()).deleteAll(any());
        }

        @Test
        void deleteProduct_WhenProductHasNoParent_ShouldNotCheckCombinations() {
            Product product = buildProduct(1L, "Product A", "product-a");
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            productService.deleteProduct(1L);

            verify(productOptionCombinationRepository, never()).findAllByProduct(any());
        }
    }

    // =========================================================
    // getProductsWithFilter
    // =========================================================
    @Nested
    class GetProductsWithFilter {

        @Test
        void getProductsWithFilter_ShouldReturnCorrectPaginationData() {
            Product product = buildProduct(1L, "Phone", "phone");
            Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
            when(productRepository.getProductsWithFilter(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(page);

            ProductListGetVm result = productService.getProductsWithFilter(0, 10, "phone", "");

            assertThat(result.productContent()).hasSize(1);
            assertThat(result.pageNo()).isEqualTo(0);
            assertThat(result.pageSize()).isEqualTo(10);
            assertThat(result.totalElements()).isEqualTo(1);
            assertThat(result.isLast()).isTrue();
        }

        @Test
        void getProductsWithFilter_WhenNoResults_ShouldReturnEmptyList() {
            Page<Product> emptyPage = new PageImpl<>(
                Collections.emptyList(), PageRequest.of(0, 10), 0);
            when(productRepository.getProductsWithFilter(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(emptyPage);

            ProductListGetVm result = productService.getProductsWithFilter(0, 10, "xyz", "");

            assertThat(result.productContent()).isEmpty();
            assertThat(result.totalElements()).isEqualTo(0);
        }

        @Test
        void getProductsWithFilter_ShouldTrimAndLowercaseProductName() {
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList());
            when(productRepository.getProductsWithFilter(eq("phone"), eq("apple"), any(Pageable.class)))
                .thenReturn(emptyPage);

            productService.getProductsWithFilter(0, 10, "  Phone  ", "apple");

            verify(productRepository)
                .getProductsWithFilter(eq("phone"), eq("apple"), any(Pageable.class));
        }
    }

    // =========================================================
    // getLatestProducts
    // =========================================================
    @Nested
    class GetLatestProducts {

        @Test
        void getLatestProducts_WhenCountPositive_ShouldReturnProducts() {
            Product product = buildProduct(1L, "Product A", "product-a");
            when(productRepository.getLatestProducts(any(Pageable.class)))
                .thenReturn(List.of(product));

            List<ProductListVm> result = productService.getLatestProducts(5);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("Product A");
        }

        @Test
        void getLatestProducts_WhenCountIsZero_ShouldReturnEmptyListWithoutQueryingDB() {
            List<ProductListVm> result = productService.getLatestProducts(0);

            assertThat(result).isEmpty();
            verifyNoInteractions(productRepository);
        }

        @Test
        void getLatestProducts_WhenCountIsNegative_ShouldReturnEmptyListWithoutQueryingDB() {
            List<ProductListVm> result = productService.getLatestProducts(-5);

            assertThat(result).isEmpty();
            verifyNoInteractions(productRepository);
        }

        @Test
        void getLatestProducts_WhenRepositoryReturnsEmpty_ShouldReturnEmptyList() {
            when(productRepository.getLatestProducts(any(Pageable.class)))
                .thenReturn(Collections.emptyList());

            List<ProductListVm> result = productService.getLatestProducts(10);

            assertThat(result).isEmpty();
        }
    }

    // =========================================================
    // getProductsByBrand
    // =========================================================
    @Nested
    class GetProductsByBrand {

        @Test
        void getProductsByBrand_WhenBrandExists_ShouldReturnThumbnailList() {
            Brand brand = new Brand();
            brand.setId(1L);
            brand.setSlug("apple");
            Product product = buildProduct(1L, "iPhone", "iphone");
            product.setThumbnailMediaId(10L);

            when(brandRepository.findBySlug("apple")).thenReturn(Optional.of(brand));
            when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(brand))
                .thenReturn(List.of(product));
            when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "http://thumb.url"));

            List<ProductThumbnailVm> result = productService.getProductsByBrand("apple");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("iPhone");
            assertThat(result.get(0).slug()).isEqualTo("iphone");
        }

        @Test
        void getProductsByBrand_WhenBrandNotFound_ShouldThrowNotFoundException() {
            when(brandRepository.findBySlug("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.getProductsByBrand("unknown"))
                .isInstanceOf(NotFoundException.class);
        }

        @Test
        void getProductsByBrand_WhenNoProducts_ShouldReturnEmptyList() {
            Brand brand = new Brand();
            brand.setSlug("empty-brand");
            when(brandRepository.findBySlug("empty-brand")).thenReturn(Optional.of(brand));
            when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(brand))
                .thenReturn(Collections.emptyList());

            List<ProductThumbnailVm> result = productService.getProductsByBrand("empty-brand");

            assertThat(result).isEmpty();
        }
    }

    // =========================================================
    // getProductSlug
    // =========================================================
    @Nested
    class GetProductSlug {

        @Test
        void getProductSlug_WhenNoParent_ShouldReturnOwnSlugAndNullProductId() {
            Product product = buildProduct(1L, "Product A", "product-a");
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            ProductSlugGetVm result = productService.getProductSlug(1L);

            assertThat(result.slug()).isEqualTo("product-a");
            assertThat(result.productVariantId()).isNull();
        }

        @Test
        void getProductSlug_WhenHasParent_ShouldReturnParentSlugAndVariationId() {
            Product parent = buildProduct(2L, "Parent", "parent-slug");
            Product product = buildProduct(1L, "Variation", "variation-slug");
            product.setParent(parent);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            ProductSlugGetVm result = productService.getProductSlug(1L);

            assertThat(result.slug()).isEqualTo("parent-slug");
            assertThat(result.productVariantId()).isEqualTo(1L);
        }

        @Test
        void getProductSlug_WhenProductNotFound_ShouldThrowNotFoundException() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.getProductSlug(99L))
                .isInstanceOf(NotFoundException.class);
        }
    }

    // =========================================================
    // getProductDetail (by slug)
    // =========================================================
    @Nested
    class GetProductDetail {

        @Test
        void getProductDetail_WhenSlugExists_ShouldReturnDetail() {
            Product product = buildProduct(1L, "Phone", "phone-slug");
            product.setThumbnailMediaId(10L);
            when(productRepository.findBySlugAndIsPublishedTrue("phone-slug"))
                .thenReturn(Optional.of(product));
            when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "http://thumb.url"));

            var result = productService.getProductDetail("phone-slug");

            assertThat(result.name()).isEqualTo("Phone");
            assertThat(result.thumbnailMediaUrl()).isEqualTo("http://thumb.url");
        }

        @Test
        void getProductDetail_WhenSlugNotFound_ShouldThrowNotFoundException() {
            when(productRepository.findBySlugAndIsPublishedTrue("unknown"))
                .thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.getProductDetail("unknown"))
                .isInstanceOf(NotFoundException.class);
        }

        @Test
        void getProductDetail_WhenHasBrand_ShouldReturnBrandName() {
            Product product = buildProduct(1L, "Phone", "phone-slug");
            product.setThumbnailMediaId(10L);
            Brand brand = new Brand();
            brand.setName("Samsung");
            product.setBrand(brand);
            when(productRepository.findBySlugAndIsPublishedTrue("phone-slug"))
                .thenReturn(Optional.of(product));
            when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "http://thumb.url"));

            var result = productService.getProductDetail("phone-slug");

            assertThat(result.brandName()).isEqualTo("Samsung");
        }
    }

    // =========================================================
    // getListFeaturedProducts
    // =========================================================
    @Nested
    class GetListFeaturedProducts {

        @Test
        void getListFeaturedProducts_ShouldReturnFeaturedVm() {
            Product product = buildProduct(1L, "Featured", "featured");
            product.setThumbnailMediaId(10L);
            Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 5), 1);
            when(productRepository.getFeaturedProduct(any(Pageable.class))).thenReturn(page);
            when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "http://thumb.url"));

            ProductFeatureGetVm result = productService.getListFeaturedProducts(0, 5);

            assertThat(result.productList()).hasSize(1);
            assertThat(result.totalPage()).isEqualTo(1);
        }

        @Test
        void getListFeaturedProducts_WhenEmpty_ShouldReturnEmptyList() {
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList());
            when(productRepository.getFeaturedProduct(any(Pageable.class))).thenReturn(emptyPage);

            ProductFeatureGetVm result = productService.getListFeaturedProducts(0, 5);

            assertThat(result.productList()).isEmpty();
        }
    }

    // =========================================================
    // exportProducts
    // =========================================================
    @Nested
    class ExportProducts {

        @Test
        void exportProducts_ShouldReturnExportingDetailVmList() {
            Product product = buildProduct(1L, "Export Product", "export-product");
            Brand brand = new Brand();
            brand.setId(1L);
            brand.setName("BrandX");
            product.setBrand(brand);
            when(productRepository.getExportingProducts(anyString(), anyString()))
                .thenReturn(List.of(product));

            List<ProductExportingDetailVm> result = productService.exportProducts("export", "");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("Export Product");
            assertThat(result.get(0).brandName()).isEqualTo("BrandX");
        }

        @Test
        void exportProducts_WhenNoProducts_ShouldReturnEmptyList() {
            when(productRepository.getExportingProducts(anyString(), anyString()))
                .thenReturn(Collections.emptyList());

            List<ProductExportingDetailVm> result = productService.exportProducts("xyz", "");

            assertThat(result).isEmpty();
        }

        @Test
        void exportProducts_ShouldTrimAndLowercaseProductName() {
            when(productRepository.getExportingProducts(eq("phone"), eq("apple")))
                .thenReturn(Collections.emptyList());

            productService.exportProducts("  Phone  ", "apple");

            verify(productRepository).getExportingProducts(eq("phone"), eq("apple"));
        }
    }

    // =========================================================
    // getRelatedProductsBackoffice
    // =========================================================
    @Nested
    class GetRelatedProductsBackoffice {

        @Test
        void getRelatedProductsBackoffice_WhenProductExists_ShouldReturnRelatedList() {
            Product main = buildProduct(1L, "Main", "main");
            Product related = buildProduct(2L, "Related", "related");
            ProductRelated pr = ProductRelated.builder()
                .product(main).relatedProduct(related).build();
            main.setRelatedProducts(List.of(pr));
            when(productRepository.findById(1L)).thenReturn(Optional.of(main));

            List<ProductListVm> result = productService.getRelatedProductsBackoffice(1L);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("Related");
        }

        @Test
        void getRelatedProductsBackoffice_WhenProductNotFound_ShouldThrowNotFoundException() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.getRelatedProductsBackoffice(99L))
                .isInstanceOf(NotFoundException.class);
        }

        @Test
        void getRelatedProductsBackoffice_WhenNoRelatedProducts_ShouldReturnEmptyList() {
            Product product = buildProduct(1L, "Main", "main");
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            List<ProductListVm> result = productService.getRelatedProductsBackoffice(1L);

            assertThat(result).isEmpty();
        }
    }

    // =========================================================
    // getProductEsDetailById
    // =========================================================
    @Nested
    class GetProductEsDetailById {

        @Test
        void getProductEsDetailById_WhenExists_ShouldReturnEsDetail() {
            Product product = buildProduct(1L, "P1", "p1");
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            var result = productService.getProductEsDetailById(1L);

            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.name()).isEqualTo("P1");
            assertThat(result.slug()).isEqualTo("p1");
        }

        @Test
        void getProductEsDetailById_WhenNotFound_ShouldThrowNotFoundException() {
            when(productRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.getProductEsDetailById(99L))
                .isInstanceOf(NotFoundException.class);
        }

        @Test
        void getProductEsDetailById_WhenHasBrand_ShouldReturnBrandName() {
            Product product = buildProduct(1L, "P1", "p1");
            Brand brand = new Brand();
            brand.setName("Samsung");
            product.setBrand(brand);
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            var result = productService.getProductEsDetailById(1L);

            assertThat(result.brand()).isEqualTo("Samsung");
        }

        @Test
        void getProductEsDetailById_WhenNoBrand_ShouldReturnNullBrandName() {
            Product product = buildProduct(1L, "P1", "p1");
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            var result = productService.getProductEsDetailById(1L);

            assertThat(result.brand()).isNull();
        }

        @Test
        void getProductEsDetailById_WhenHasCategories_ShouldReturnCategoryNames() {
            Product product = buildProduct(1L, "P1", "p1");
            Category cat = new Category();
            cat.setName("Electronics");
            ProductCategory pc = ProductCategory.builder()
                .product(product).category(cat).build();
            product.setProductCategories(List.of(pc));
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            var result = productService.getProductEsDetailById(1L);

            assertThat(result.categories()).containsExactly("Electronics");
        }
    }

    // =========================================================
    // updateProductQuantity
    // =========================================================
    @Nested
    class UpdateProductQuantity {

        @Test
        void updateProductQuantity_ShouldUpdateStockQuantityToGivenValue() {
            Product product = buildProduct(1L, "P1", "p1");
            product.setStockQuantity(10L);
            when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));

            productService.updateProductQuantity(List.of(new ProductQuantityPostVm(1L, 50L)));

            assertThat(product.getStockQuantity()).isEqualTo(50L);
            verify(productRepository).saveAll(List.of(product));
        }

        @Test
        void updateProductQuantity_WhenMultipleProducts_ShouldUpdateEachIndependently() {
            Product p1 = buildProduct(1L, "P1", "p1");
            Product p2 = buildProduct(2L, "P2", "p2");
            when(productRepository.findAllByIdIn(List.of(1L, 2L))).thenReturn(List.of(p1, p2));

            productService.updateProductQuantity(List.of(
                new ProductQuantityPostVm(1L, 20L),
                new ProductQuantityPostVm(2L, 30L)
            ));

            verify(productRepository).saveAll(anyList());
        }
    }

    // =========================================================
    // subtractStockQuantity
    // =========================================================
    @Nested
    class SubtractStockQuantity {

        @Test
        void subtractStockQuantity_WhenSufficientStock_ShouldSubtract() {
            Product product = buildProduct(1L, "P1", "p1");
            product.setStockTrackingEnabled(true);
            product.setStockQuantity(100L);
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));

            productService.subtractStockQuantity(List.of(new ProductQuantityPutVm(1L, 30L)));

            assertThat(product.getStockQuantity()).isEqualTo(70L);
        }

        @Test
        void subtractStockQuantity_WhenInsufficientStock_ShouldSetToZero() {
            Product product = buildProduct(1L, "P1", "p1");
            product.setStockTrackingEnabled(true);
            product.setStockQuantity(5L);
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));

            productService.subtractStockQuantity(List.of(new ProductQuantityPutVm(1L, 100L)));

            assertThat(product.getStockQuantity()).isEqualTo(0L);
        }

        @Test
        void subtractStockQuantity_WhenTrackingDisabled_ShouldNotModifyQuantity() {
            Product product = buildProduct(1L, "P1", "p1");
            product.setStockTrackingEnabled(false);
            product.setStockQuantity(50L);
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));

            productService.subtractStockQuantity(List.of(new ProductQuantityPutVm(1L, 10L)));

            assertThat(product.getStockQuantity()).isEqualTo(50L);
        }
    }

    // =========================================================
    // restoreStockQuantity
    // =========================================================
    @Nested
    class RestoreStockQuantity {

        @Test
        void restoreStockQuantity_WhenTrackingEnabled_ShouldAddQuantity() {
            Product product = buildProduct(1L, "P1", "p1");
            product.setStockTrackingEnabled(true);
            product.setStockQuantity(20L);
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));

            productService.restoreStockQuantity(List.of(new ProductQuantityPutVm(1L, 15L)));

            assertThat(product.getStockQuantity()).isEqualTo(35L);
        }

        @Test
        void restoreStockQuantity_WhenTrackingDisabled_ShouldNotModifyQuantity() {
            Product product = buildProduct(1L, "P1", "p1");
            product.setStockTrackingEnabled(false);
            product.setStockQuantity(20L);
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));

            productService.restoreStockQuantity(List.of(new ProductQuantityPutVm(1L, 15L)));

            assertThat(product.getStockQuantity()).isEqualTo(20L);
        }

        @Test
        void restoreStockQuantity_WhenDuplicateIds_ShouldMergeAndAdd() {
            Product product = buildProduct(1L, "P1", "p1");
            product.setStockTrackingEnabled(true);
            product.setStockQuantity(10L);
            when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));

            // 2 items cùng id: 5 + 5 = 10 → 10 + 10 = 20
            productService.restoreStockQuantity(List.of(
                new ProductQuantityPutVm(1L, 5L),
                new ProductQuantityPutVm(1L, 5L)
            ));

            assertThat(product.getStockQuantity()).isEqualTo(20L);
        }
    }

    // =========================================================
    // getProductByIds
    // =========================================================
    @Nested
    class GetProductByIds {

        @Test
        void getProductByIds_ShouldReturnProductListVm() {
            Product product = buildProduct(1L, "P1", "p1");
            when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));

            List<ProductListVm> result = productService.getProductByIds(List.of(1L));

            assertThat(result).hasSize(1);
            assertThat(result.get(0).name()).isEqualTo("P1");
        }

        @Test
        void getProductByIds_WhenEmptyIds_ShouldReturnEmptyList() {
            when(productRepository.findAllByIdIn(Collections.emptyList()))
                .thenReturn(Collections.emptyList());

            List<ProductListVm> result = productService.getProductByIds(Collections.emptyList());

            assertThat(result).isEmpty();
        }
    }

    // =========================================================
    // getProductByCategoryIds / getProductByBrandIds
    // =========================================================
    @Nested
    class GetProductByCategoryAndBrandIds {

        @Test
        void getProductByCategoryIds_ShouldReturnMatchingProducts() {
            Product product = buildProduct(1L, "P1", "p1");
            when(productRepository.findByCategoryIdsIn(List.of(5L))).thenReturn(List.of(product));

            List<ProductListVm> result = productService.getProductByCategoryIds(List.of(5L));

            assertThat(result).hasSize(1);
        }

        @Test
        void getProductByCategoryIds_WhenNoMatch_ShouldReturnEmptyList() {
            when(productRepository.findByCategoryIdsIn(anyList())).thenReturn(Collections.emptyList());

            List<ProductListVm> result = productService.getProductByCategoryIds(List.of(999L));

            assertThat(result).isEmpty();
        }

        @Test
        void getProductByBrandIds_ShouldReturnMatchingProducts() {
            Product product = buildProduct(1L, "P1", "p1");
            when(productRepository.findByBrandIdsIn(List.of(3L))).thenReturn(List.of(product));

            List<ProductListVm> result = productService.getProductByBrandIds(List.of(3L));

            assertThat(result).hasSize(1);
        }

        @Test
        void getProductByBrandIds_WhenNoMatch_ShouldReturnEmptyList() {
            when(productRepository.findByBrandIdsIn(anyList())).thenReturn(Collections.emptyList());

            List<ProductListVm> result = productService.getProductByBrandIds(List.of(999L));

            assertThat(result).isEmpty();
        }
    }

    // =========================================================
    // getProductsForWarehouse
    // =========================================================
    @Nested
    class GetProductsForWarehouse {

        @Test
        void getProductsForWarehouse_ShouldReturnProductInfoVmList() {
            Product product = buildProduct(1L, "P1", "p1");
            when(productRepository.findProductForWarehouse(anyString(), anyString(), any(), anyString()))
                .thenReturn(List.of(product));

            List<ProductInfoVm> result = productService.getProductsForWarehouse(
                "P1", "SKU-1", List.of(1L), FilterExistInWhSelection.ALL);

            assertThat(result).hasSize(1);
        }

        @Test
        void getProductsForWarehouse_WhenNoMatch_ShouldReturnEmptyList() {
            when(productRepository.findProductForWarehouse(anyString(), anyString(), any(), anyString()))
                .thenReturn(Collections.emptyList());

            List<ProductInfoVm> result = productService.getProductsForWarehouse(
                "none", "none", Collections.emptyList(), FilterExistInWhSelection.NO);

            assertThat(result).isEmpty();
        }
    }

    // =========================================================
    // getProductsByMultiQuery
    // =========================================================
    @Nested
    class GetProductsByMultiQuery {

        @Test
        void getProductsByMultiQuery_ShouldReturnPagedResults() {
            Product product = buildProduct(1L, "Phone", "phone");
            product.setThumbnailMediaId(10L);
            Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
            when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(
                anyString(), anyString(), any(), any(), any(Pageable.class)))
                .thenReturn(page);
            when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "http://thumb.url"));

            ProductsGetVm result = productService.getProductsByMultiQuery(0, 10, "phone", "", null, null);

            assertThat(result.productContent()).hasSize(1);
            assertThat(result.totalElements()).isEqualTo(1);
        }

        @Test
        void getProductsByMultiQuery_WhenNoResults_ShouldReturnEmptyList() {
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList());
            when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(
                anyString(), anyString(), any(), any(), any(Pageable.class)))
                .thenReturn(emptyPage);

            ProductsGetVm result = productService.getProductsByMultiQuery(0, 10, "xyz", "", 0.0, 999.0);

            assertThat(result.productContent()).isEmpty();
        }

        @Test
        void getProductsByMultiQuery_ShouldTrimAndLowercaseProductName() {
            Page<Product> emptyPage = new PageImpl<>(Collections.emptyList());
            when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(
                eq("phone"), eq("electronics"), any(), any(), any(Pageable.class)))
                .thenReturn(emptyPage);

            productService.getProductsByMultiQuery(0, 10, "  Phone  ", "electronics", null, null);

            verify(productRepository).findByProductNameAndCategorySlugAndPriceBetween(
                eq("phone"), eq("electronics"), any(), any(), any(Pageable.class));
        }
    }

    // =========================================================
    // setProductImages
    // =========================================================
    @Nested
    class SetProductImages {

        @Test
        void setProductImages_WhenImageIdsEmpty_ShouldDeleteAndReturnEmpty() {
            Product product = buildProduct(1L, "P1", "p1");

            List<ProductImage> result = productService.setProductImages(Collections.emptyList(), product);

            assertThat(result).isEmpty();
            verify(productImageRepository).deleteByProductId(product.getId());
        }

        @Test
        void setProductImages_WhenProductHasNullImages_ShouldCreateAllAsNew() {
            Product product = buildProduct(1L, "P1", "p1");
            product.setProductImages(null);

            List<ProductImage> result = productService.setProductImages(List.of(100L, 200L), product);

            assertThat(result).hasSize(2);
            assertThat(result.stream().map(ProductImage::getImageId))
                .containsExactlyInAnyOrder(100L, 200L);
        }

        @Test
        void setProductImages_WhenNewImageAdded_ShouldReturnOnlyNewImage() {
            Product product = buildProduct(1L, "P1", "p1");
            ProductImage existing = ProductImage.builder().imageId(100L).product(product).build();
            product.setProductImages(new ArrayList<>(List.of(existing)));

            // Giữ 100, thêm 200 → chỉ trả về 200 (new)
            List<ProductImage> result = productService.setProductImages(List.of(100L, 200L), product);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getImageId()).isEqualTo(200L);
        }

        @Test
        void setProductImages_WhenImageRemoved_ShouldCallDeleteByImageIdInAndProductId() {
            Product product = buildProduct(1L, "P1", "p1");
            ProductImage existing = ProductImage.builder().imageId(100L).product(product).build();
            product.setProductImages(new ArrayList<>(List.of(existing)));

            // 100 bị xóa khỏi list mới
            productService.setProductImages(List.of(200L), product);

            verify(productImageRepository)
                .deleteByImageIdInAndProductId(List.of(100L), product.getId());
        }
    }
}
