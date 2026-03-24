package com.vendo.product_service.domain.product.exception;

public class NotProductOwnerException extends RuntimeException {
    public NotProductOwnerException(String message) {
        super(message);
    }
}
