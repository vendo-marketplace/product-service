package com.vendo.product_service.domain.product_image.exception;

public class ProductImageAlreadyConfirmedException extends RuntimeException {
    public ProductImageAlreadyConfirmedException(String message) {
        super(message);
    }
}
