package com.vendo.product_service.adapter.category.in.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.List;

import static com.vendo.product_service.domain.category.constants.CategoryConstants.*;

@Builder
public record CreateCategoryRequest(

        @NotNull(message = "Title is required.")
        @Pattern(regexp = CATEGORY_TITLE_PATTERN, message = "Title validation failed.")
        String title,

        @NotNull(message = "Code is required.")
        @Pattern(regexp = CATEGORY_CODE_PATTERN, message = "Code validation failed.")
        String code,

        String parentId,

        List<String> attributes) {
}
