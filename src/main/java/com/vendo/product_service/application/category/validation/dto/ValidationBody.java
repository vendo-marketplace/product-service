package com.vendo.product_service.application.category.validation.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record ValidationBody(
        boolean valid,
        String fieldName,
        String errorMessage) {

    public static ValidationBody from(String fieldName, String errorMessage) {
        return ValidationBody.builder()
                .fieldName(fieldName)
                .errorMessage(errorMessage)
                .build();
    }

}
