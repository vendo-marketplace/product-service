package com.vendo.product_service.domain.image.exception;

public class ImageKeyNotFoundException extends RuntimeException {
    public ImageKeyNotFoundException(String message) {
        super(message);
    }
}
