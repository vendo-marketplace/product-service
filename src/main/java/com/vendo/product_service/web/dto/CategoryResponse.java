package com.vendo.product_service.web.dto;

import com.vendo.product_service.common.type.CategoryType;
import com.vendo.product_service.db.model.embedded.AttributeValue;

import java.util.Map;

public record CategoryResponse(
         String id,
         String title,
         String parentId,
         CategoryType categoryType,
         Map<String, AttributeValue> attributes) {
}
