package com.vendo.product_service.domain.category.common.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class CategoryValidationException extends RuntimeException {

    private Map<String, String> errors;

    public CategoryValidationException(String message) {
        super(message);
    }

    public CategoryValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }
}
