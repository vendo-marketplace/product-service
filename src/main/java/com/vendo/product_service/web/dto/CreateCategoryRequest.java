package com.vendo.product_service.web.dto;

import com.vendo.product_service.common.type.CategoryType;
import com.vendo.product_service.db.model.embedded.AttributeValue;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;

@Builder
public record CreateCategoryRequest(
        @NotNull(message = "Title is required.")
        String title,

        String parentId,

        @NotNull(message = "Category type is required.")
        CategoryType categoryType,

        Map<String, AttributeValue> attributes) {
}
