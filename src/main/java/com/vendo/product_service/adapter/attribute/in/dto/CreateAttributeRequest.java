package com.vendo.product_service.adapter.attribute.in.dto;

import com.vendo.product_service.domain.product.pattern.ProductPatterns;
import com.vendo.product_service.domain.attribute.model.AttributeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateAttributeRequest(

        @NotNull(message = "Title is required.")
        @Pattern(regexp = ProductPatterns.ATTRIBUTE_TITLE_PATTERN, message = ProductPatterns.ATTRIBUTE_TITLE_VALIDATION_MESSAGE)
        String title,

        @NotNull(message = "Slug is required.")
        @Pattern(regexp = ProductPatterns.SLUG_PATTERN, message = ProductPatterns.SLUG_VALIDATION_MESSAGE)
        String slug,

        @NotNull(message = "Type is required.")
        AttributeType type,

        boolean required,
        List<String> allowedValues
) {
}
