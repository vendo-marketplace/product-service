package com.vendo.product_service.adapter.in.category.dto;

import com.vendo.product_service.domain.category.model.CategoryType;
import com.vendo.product_service.adapter.model.category.embedded.AttributeDefinition;

import java.util.Map;

public record CategoryEntityResponse(
         String id,
         String title,
         String parentId,
         CategoryType categoryType,
         Map<String, AttributeDefinition> attributes) {
}
