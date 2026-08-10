package com.vendo.product_service.domain.category.exception;

public class CategoryHasNoImageException extends RuntimeException {
    public CategoryHasNoImageException(String message) {
        super(message);
    }
}
