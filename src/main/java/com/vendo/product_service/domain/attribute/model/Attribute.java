package com.vendo.product_service.domain.attribute.model;

import java.util.List;

public record Attribute(
        String id,
        String title,
        AttributeType type,
        boolean required,
        List<String> allowedValues
) {
}
