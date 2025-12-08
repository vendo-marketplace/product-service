package com.vendo.product_service.domain.category.web.dto;

import com.vendo.product_service.domain.category.model.embedded.AttributeValue;

import java.util.Map;

public record CategoryResponse(
         String id,
         String title,
         String parentId,
         Map<String, AttributeValue> attributes) {
}
