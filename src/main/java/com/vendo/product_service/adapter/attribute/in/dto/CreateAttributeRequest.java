package com.vendo.product_service.adapter.attribute.in.dto;

import com.vendo.product_service.domain.attribute.model.AttributeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.List;

import static com.vendo.product_service.domain.attribute.constants.AttributeConstants.ATTRIBUTE_NAME_PATTERN;

@Builder
public record CreateAttributeRequest(

        @Pattern(regexp = ATTRIBUTE_NAME_PATTERN, message = "Attribute name validation failed.")
        String title,

        @NotNull(message = "Type is required.")
        AttributeType type,

        boolean required,
        List<String> allowedValues
) {
}
