package com.vendo.product_service.adapter.category.in.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.List;

import static com.vendo.product_service.domain.category.constants.CategoryConstants.CATEGORY_TITLE_PATTERN;
import static com.vendo.product_service.domain.product.constants.ProductConstants.SLUG_PATTERN;

@Builder
public record CreateCategoryRequest(

        @NotNull(message = "Title is required.")
        @Pattern(regexp = CATEGORY_TITLE_PATTERN, message = "Title validation failed.")
        String title,

        @NotNull(message = "Slug is required.")
        @Pattern(regexp = SLUG_PATTERN, message = "Slug validation failed.")
        String slug,

        String parentId,

        List<String> attributes) {
}
