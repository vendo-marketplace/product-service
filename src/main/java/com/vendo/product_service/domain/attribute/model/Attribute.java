package com.vendo.product_service.domain.attribute.model;

import lombok.Builder;

import java.util.List;

@Builder
public record Attribute(
        String id,
        String title,
        AttributeType type,
        boolean required,
        List<String> allowedValues
) {
}
