package com.vendo.product_service.domain.image.exception;

public class EmptyImageException extends RuntimeException {
    public EmptyImageException(String message) {
        super(message);
    }
}
