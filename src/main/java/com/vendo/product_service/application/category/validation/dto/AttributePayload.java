package com.vendo.product_service.application.category.validation.dto;

import com.vendo.product_service.domain.category.model.AttributeDefinition;

public record AttributePayload(String name, AttributeDefinition definition) {
}
