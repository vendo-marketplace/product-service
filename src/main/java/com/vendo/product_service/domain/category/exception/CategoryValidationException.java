package com.vendo.product_service.domain.category.exception;

import java.util.Map;

public class CategoryValidationException extends RuntimeException {

    private Map<String, String> errors;

    public CategoryValidationException(String message) {
        super(message);
    }

    public CategoryValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
