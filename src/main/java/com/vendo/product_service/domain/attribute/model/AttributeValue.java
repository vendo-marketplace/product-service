package com.vendo.product_service.domain.attribute.model;

import java.util.List;

public record AttributeValue(String id, List<String> values) {

    public Attribute getById(List<Attribute> attributes) {
        return attributes.stream()
                .filter(attribute -> attribute.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Attribute %s not found.".formatted(id)));
    }

}
