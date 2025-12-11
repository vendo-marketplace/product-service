package com.vendo.product_service.domain.category.web.dto;

import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeValue;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record CreateCategoryRequest(
        @NotNull(message = "Title is required.")
        String title,

        String parentId,

        @NotNull(message = "Category type is required.")
        CategoryType categoryType,

        Map<String, AttributeValue> attributes) {
}
