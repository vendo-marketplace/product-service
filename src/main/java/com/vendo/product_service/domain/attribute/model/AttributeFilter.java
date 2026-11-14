package com.vendo.product_service.domain.attribute.model;

import lombok.Builder;

@Builder
public record AttributeFilter(String search, AttributeType type) {
}
