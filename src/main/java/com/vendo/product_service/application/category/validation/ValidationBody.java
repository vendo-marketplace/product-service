package com.vendo.product_service.application.category.validation;

import lombok.Builder;

@Builder(toBuilder = true)
public record ValidationBody(
        boolean valid,
        String fieldName,
        String errorMessage) {
}
