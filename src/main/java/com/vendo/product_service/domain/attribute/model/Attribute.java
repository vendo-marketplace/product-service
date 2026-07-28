package com.vendo.product_service.domain.attribute.model;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record Attribute(
        String id,
        String title,
        String slug,
        AttributeType type,
        boolean required,
        List<String> allowedValues
) {

    public static List<Attribute> extractAll(List<String> ids, Map<String, Attribute> attributesById) {
        if (ids.isEmpty()) return List.of();
        return ids.stream().map(attributesById::get).toList();
    }

}
