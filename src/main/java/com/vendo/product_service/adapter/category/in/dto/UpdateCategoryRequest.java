package com.vendo.product_service.adapter.category.in.dto;

import com.vendo.product_service.domain.product.pattern.ProductPatterns;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record UpdateCategoryRequest(
        @Pattern(regexp = ProductPatterns.CATEGORY_TITLE_PATTERN, message = ProductPatterns.CATEGORY_TITLE_VALIDATION_MESSAGE)
        String title,

        @Pattern(regexp = ProductPatterns.SLUG_PATTERN, message = ProductPatterns.SLUG_VALIDATION_MESSAGE)
        String slug,
        String parentId,
        List<String> attributes
) {
}