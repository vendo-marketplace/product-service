package com.vendo.product_service.common.exception;

import lombok.Builder;

@Builder(toBuilder = true)
public record ValidationBody (
        boolean valid,
        String fieldName,
        String errorMessage) {
}
