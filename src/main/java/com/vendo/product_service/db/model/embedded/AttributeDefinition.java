package com.vendo.product_service.db.model.embedded;

import lombok.Builder;

import java.util.List;

@Builder
public record AttributeDefinition(
     AttributeType type,
     boolean required,
     List<String> allowedValues) {
}
