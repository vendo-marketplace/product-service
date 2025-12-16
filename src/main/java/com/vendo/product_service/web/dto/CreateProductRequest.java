package com.vendo.product_service.web.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
public record CreateProductRequest(
        @NotBlank(message = "Title is required.")
        @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters.")
        String title,

        @NotBlank(message = "Description is required.")
        @Size(min = 5, max = 250, message = "Description must be between 5 and 250 characters.")
        String description,

        @Min(value = 0, message = "Minimal quantity is one.")
        int quantity,

        @NotNull(message = "Price is required.")
        @DecimalMin(value = "0", inclusive = false, message = "Price must be greater or equal to 0.")
        @Digits(integer = 8, fraction = 2, message = "Price must have up to 8 digits before the decimal point and 2 after.")
        BigDecimal price,

        @NotNull(message = "Category id is required.")
        String categoryId,

        @NotEmpty(message = "Minimum 1 attribute is required.")
        Map<String, Object> attributes) {
}
