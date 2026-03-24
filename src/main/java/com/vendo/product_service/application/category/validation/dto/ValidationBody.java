package com.vendo.product_service.application.category.validation.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record ValidationBody(
        boolean valid,
        String fieldName,
        String errorMessage) {
}
