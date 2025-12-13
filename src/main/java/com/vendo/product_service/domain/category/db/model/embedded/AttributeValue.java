package com.vendo.product_service.domain.category.db.model.embedded;

import lombok.Builder;

import java.util.List;

@Builder
public record AttributeValue(
     String type,
     boolean required,
     List<String> allowedValues) {
}
