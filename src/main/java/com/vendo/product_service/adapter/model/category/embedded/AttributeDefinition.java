package com.vendo.product_service.adapter.model.category.embedded;

import lombok.Builder;

import java.util.List;

@Builder
public record AttributeDefinition(
     AttributeType type,
     boolean required,
     List<String> allowedValues) {
}
