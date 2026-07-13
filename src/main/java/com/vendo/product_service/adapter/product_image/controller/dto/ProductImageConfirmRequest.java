package com.vendo.product_service.adapter.product_image.controller.dto;

import com.vendo.product_service.domain.product.pattern.ProductPatterns;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record ProductImageConfirmRequest(
        @NotEmpty(message = "Keys are required.")
        List<@Pattern(regexp = ProductPatterns.PHOTO_IMAGE_KEY_PATTERN, message = ProductPatterns.PHOTO_IMAGE_KEY_MESSAGE) String> keys
) {
}
