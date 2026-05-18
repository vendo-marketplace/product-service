package com.vendo.product_service.domain.attribute.model;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record Attribute(
        String id,
        String title,
        AttributeType type,
        boolean required,
        List<String> allowedValues
) {

    public static Attribute getById(String id, List<Attribute> attributes) {
        return attributes.stream()
                .filter(attribute -> attribute.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Attribute %s not found.".formatted(id)));
    }

    public static List<Attribute> extractAttributes(List<String> ids, Map<String, Attribute> attributesById) {
        if (ids.isEmpty()) return List.of();
        return ids.stream().map(attributesById::get).toList();
    }

}
