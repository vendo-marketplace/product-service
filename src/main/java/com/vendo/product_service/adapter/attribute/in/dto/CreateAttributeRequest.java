package com.vendo.product_service.adapter.attribute.in.dto;

import com.vendo.product_service.domain.attribute.model.AttributeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.List;

import static com.vendo.product_service.domain.attribute.constants.AttributeConstants.ATTRIBUTE_NAME_PATTERN;
import static com.vendo.product_service.domain.product.constants.ProductConstants.SLUG_PATTERN;

@Builder
public record CreateAttributeRequest(

        @NotNull(message = "Title is required.")
        @Pattern(regexp = ATTRIBUTE_NAME_PATTERN, message = "Title validation failed.")
        String title,

        @NotNull(message = "Slug is required.")
        @Pattern(regexp = SLUG_PATTERN, message = "Slug validation failed.")
        String slug,

        @NotNull(message = "Type is required.")
        AttributeType type,

        boolean required,
        List<String> allowedValues
) {
}
