package com.yas.product.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstantsTest {

    @Test
    void errorCode_ShouldDefineProductNotFound() {
        assertEquals("PRODUCT_NOT_FOUND", Constants.ErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    void errorCode_ShouldDefineProductOptionNotFound() {
        assertEquals("Product option %s is not found", Constants.ErrorCode.PRODUCT_OPTION_NOT_FOUND);
    }

    @Test
    void errorCode_ShouldDefineProductAttributeNotFound() {
        assertEquals("Product attribute %s is not found", Constants.ErrorCode.PRODUCT_ATTRIBUTE_NOT_FOUND);
    }

    @Test
    void errorCode_ShouldDefineProductAttributeGroupNotFound() {
        assertEquals("Product attribute group %s is not found", Constants.ErrorCode.PRODUCT_ATTRIBUTE_GROUP_NOT_FOUND);
    }

    @Test
    void errorCode_ShouldDefineCategoryNotFound() {
        assertEquals("CATEGORY_NOT_FOUND", Constants.ErrorCode.CATEGORY_NOT_FOUND);
    }

    @Test
    void errorCode_ShouldDefineBrandNotFound() {
        assertEquals("BRAND_NOT_FOUND", Constants.ErrorCode.BRAND_NOT_FOUND);
    }

    @Test
    void errorCode_ShouldDefineParentCategoryNotFound() {
        assertEquals("PARENT_CATEGORY_NOT_FOUND", Constants.ErrorCode.PARENT_CATEGORY_NOT_FOUND);
    }

    @Test
    void errorCode_ShouldDefineMakeSureCategoryDoNotContainChildren() {
        assertEquals(
                "MAKE_SURE_CATEGORY_DO_NOT_CONTAIN_CHILDREN",
                Constants.ErrorCode.MAKE_SURE_CATEGORY_DO_NOT_CONTAIN_CHILDREN
        );
    }

    @Test
    void errorCode_ShouldDefineMakeSureCategoryDoNotContainProduct() {
        assertEquals(
                "MAKE_SURE_CATEGORY_DO_NOT_CONTAIN_PRODUCT",
                Constants.ErrorCode.MAKE_SURE_CATEGORY_DO_NOT_CONTAIN_PRODUCT
        );
    }

    @Test
    void errorCode_ShouldDefineParentCategoryCannotBeItself() {
        assertEquals("PARENT_CATEGORY_CANNOT_BE_ITSELF", Constants.ErrorCode.PARENT_CATEGORY_CANNOT_BE_ITSELF);
    }

    @Test
    void errorCode_ShouldDefineProductAttributeValueIsNotFound() {
        assertEquals("PRODUCT_ATTRIBUTE_VALUE_IS_NOT_FOUND", Constants.ErrorCode.PRODUCT_ATTRIBUTE_VALUE_IS_NOT_FOUND);
    }

    @Test
    void errorCode_ShouldDefineSlugIsDuplicated() {
        assertEquals("SLUG_IS_DUPLICATED", Constants.ErrorCode.SLUG_IS_DUPLICATED);
    }

    @Test
    void errorCode_ShouldDefineProductTemplateIsNotFound() {
        assertEquals("PRODUCT_TEMPLATE_IS_NOT_FOUND", Constants.ErrorCode.PRODUCT_TEMPlATE_IS_NOT_FOUND);
    }

    @Test
    void errorCode_ShouldDefineMakeSureBrandDontContainsAnyProduct() {
        assertEquals(
                "MAKE_SURE_BRAND_DONT_CONTAINS_ANY_PRODUCT",
                Constants.ErrorCode.MAKE_SURE_BRAND_DONT_CONTAINS_ANY_PRODUCT
        );
    }

    @Test
    void errorCode_ShouldDefineMakeSureProductAttributeGroupDoNotContainsAnyProductAttribute() {
        assertEquals(
                "Make sure product attribute group do not contains any product attribute",
                Constants.ErrorCode.MAKE_SURE_PRODUCT_ATTRIBUTE_GROUP_DO_NOT_CONTAINS_ANY_PRODUCT_ATTRIBUTE
        );
    }

    @Test
    void errorCode_ShouldDefineNameAlreadyExited() {
        assertEquals("NAME_ALREADY_EXITED", Constants.ErrorCode.NAME_ALREADY_EXITED);
    }

    @Test
    void errorCode_ShouldDefineSlugAlreadyExistedOrDuplicated() {
        assertEquals("SLUG_ALREADY_EXISTED_OR_DUPLICATED", Constants.ErrorCode.SLUG_ALREADY_EXISTED_OR_DUPLICATED);
    }

    @Test
    void errorCode_ShouldDefineSkuAlreadyExistedOrDuplicated() {
        assertEquals("SKU_ALREADY_EXISTED_OR_DUPLICATED", Constants.ErrorCode.SKU_ALREADY_EXISTED_OR_DUPLICATED);
    }

    @Test
    void errorCode_ShouldDefineGtinAlreadyExistedOrDuplicated() {
        assertEquals("GTIN_ALREADY_EXISTED_OR_DUPLICATED", Constants.ErrorCode.GTIN_ALREADY_EXISTED_OR_DUPLICATED);
    }

    @Test
    void errorCode_ShouldDefineProductOptionValueIsNotFound() {
        assertEquals("PRODUCT_OPTION_VALUE_IS_NOT_FOUND", Constants.ErrorCode.PRODUCT_OPTION_VALUE_IS_NOT_FOUND);
    }

    @Test
    void errorCode_ShouldDefineProductCombinationProcessingFailed() {
        assertEquals("PRODUCT_COMBINATION_PROCESSING_FAILED", Constants.ErrorCode.PRODUCT_COMBINATION_PROCESSING_FAILED);
    }

    @Test
    void errorCode_ShouldDefineNoMatchingProductOptions() {
        assertEquals("NO_MATCHING_PRODUCT_OPTIONS", Constants.ErrorCode.NO_MATCHING_PRODUCT_OPTIONS);
    }

    @Test
    void errorCode_ShouldDefineMakeSureLengthGreaterThanWidth() {
        assertEquals("MAKE_SURE_LENGTH_GREATER_THAN_WIDTH", Constants.ErrorCode.MAKE_SURE_LENGTH_GREATER_THAN_WIDTH);
    }
}
