package com.vendo.product_service.adapter.in.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.vendo.product_service.domain.category.validation.attribute.CategoryConstants.CATEGORY_ATTRIBUTE_NAME_PATTERN;

@Builder
public record UpdateProductRequest(
        @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters.")
        String title,

        @Size(min = 5, max = 250, message = "Description must be between 5 and 250 characters.")
        String description,

        Integer quantity,

        @DecimalMin(value = "0", inclusive = false, message = "Price must be greater or equal to 0.")
        @Digits(integer = 8, fraction = 2, message = "Price must have up to 8 digits before the decimal point and 2 after.")
        BigDecimal price,

        String categoryId,

        Map<@Pattern(regexp = CATEGORY_ATTRIBUTE_NAME_PATTERN, message = "Attribute name validation failed.") String, List<String>> attributes,

        Boolean active
) {
}
