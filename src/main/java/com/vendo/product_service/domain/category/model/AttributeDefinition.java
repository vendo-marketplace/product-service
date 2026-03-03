package com.vendo.product_service.domain.category.model;

import lombok.Builder;

import java.util.List;

@Builder
public record AttributeDefinition(
     AttributeType type,
     boolean required,
     List<String> allowedValues) {
}
