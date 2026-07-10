package com.vendo.product_service.domain.product_image.exception;

public class ProductImageAlreadyExists extends RuntimeException {
    public ProductImageAlreadyExists(String message) {
        super(message);
    }
}
