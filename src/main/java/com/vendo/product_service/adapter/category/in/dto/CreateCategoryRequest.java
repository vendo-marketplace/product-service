package com.vendo.product_service.adapter.category.in.dto;

import com.vendo.product_service.domain.product.pattern.ProductPatterns;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateCategoryRequest(

        @NotNull(message = "Title is required.")
        @Pattern(regexp = ProductPatterns.TITLE_PATTERN, message = ProductPatterns.TITLE_VALIDATION_MESSAGE)
        String title,

        @NotNull(message = "Slug is required.")
        @Pattern(regexp = ProductPatterns.SLUG_PATTERN, message = ProductPatterns.SLUG_VALIDATION_MESSAGE)
        String slug,

        String parentId,

        List<String> attributes) {
}
