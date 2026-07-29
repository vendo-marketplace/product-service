package com.vendo.product_service.adapter.product.in.dto;

import com.vendo.product_service.domain.attribute.model.AttributeValue;
import com.vendo.product_service.domain.product.pattern.ProductPatterns;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record UpdateProductRequest(

        @Pattern(regexp = ProductPatterns.PRODUCT_TITLE_PATTERN, message = ProductPatterns.PRODUCT_TITLE_VALIDATION_MESSAGE)
        String title,

        @Size(min = 5, max = 250, message = "Description must be between 5 and 250 characters.")
        String description,

        Integer quantity,

        Boolean isNew,

        @DecimalMin(value = "0", inclusive = false, message = "Price must be greater or equal to 0.")
        @Digits(integer = 8, fraction = 2, message = "Price must have up to 8 digits before the decimal point and 2 after.")
        BigDecimal price,

        String categoryId,

        List<AttributeValue> attributes,

        Boolean active
) {
}
