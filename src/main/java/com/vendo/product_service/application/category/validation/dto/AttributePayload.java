package com.vendo.product_service.application.category.validation.dto;

import com.vendo.product_service.adapter.model.category.embedded.AttributeDefinition;

public record AttributePayload(String name, AttributeDefinition definition) {
}
