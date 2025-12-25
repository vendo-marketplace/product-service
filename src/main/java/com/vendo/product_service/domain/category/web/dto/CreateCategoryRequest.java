package com.vendo.product_service.domain.category.web.dto;

import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Map;

@Builder
public record CreateCategoryRequest(
        @NotNull(message = "Title is required.")
        String title,

        String parentId,

        @NotNull(message = "Code is required.")
        String code,

        Map<String, AttributeDefinition> attributes) {
}
