package com.vendo.product_service.adapter.category.in.dto;

import com.vendo.product_service.domain.category.model.AttributeDefinition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.Map;

import static com.vendo.product_service.domain.category.constants.CategoryConstants.*;

@Builder
public record CreateCategoryRequest(

        @NotBlank(message = "Title is required.")
        @Pattern(regexp = CATEGORY_TITLE_PATTERN, message = "Title validation failed.")
        String title,

        @NotBlank(message = "Code is required.")
        @Pattern(regexp = CATEGORY_CODE_PATTERN, message = "Code validation failed.")
        String code,

        String parentId,

        Map<@Pattern(regexp = CATEGORY_ATTRIBUTE_NAME_PATTERN, message = "Attribute name validation failed.") String, AttributeDefinition> attributes) {
}
