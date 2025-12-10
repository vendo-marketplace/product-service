package com.vendo.product_service.domain.category.web.dto;

import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.model.embedded.AttributeValue;

import java.util.Map;

public record CategoryResponse(
         String id,
         String title,
         String parentId,
         CategoryType categoryType,
         Map<String, AttributeValue> attributes) {
}
