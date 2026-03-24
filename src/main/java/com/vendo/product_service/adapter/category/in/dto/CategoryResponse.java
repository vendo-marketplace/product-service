package com.vendo.product_service.adapter.category.in.dto;

import com.vendo.product_service.domain.category.model.AttributeDefinition;
import com.vendo.product_service.domain.category.type.CategoryType;

import java.util.Map;

public record CategoryResponse(
         String id,
         String title,
         String parentId,
         CategoryType categoryType,
         Map<String, AttributeDefinition> attributes) {
}
