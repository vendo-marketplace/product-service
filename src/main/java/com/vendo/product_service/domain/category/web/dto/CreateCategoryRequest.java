package com.vendo.product_service.domain.category.web.dto;

import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.Map;

import static com.vendo.product_service.domain.category.common.constants.CategoryConstants.CATEGORY_ATTRIBUTE_NAME_PATTERN;

@Builder
public record CreateCategoryRequest(

        @NotBlank(message = "Title is required.")
        String title,

        String parentId,

        @NotBlank(message = "Code is required.")
        String code,

        Map<@Pattern(regexp = CATEGORY_ATTRIBUTE_NAME_PATTERN, message = "Attribute name validation failed: invalid capitalization or separators.") String, AttributeDefinition> attributes) {
}
