package com.vendo.product_service.domain.image.exception;

public class ImagesLimitExceededException extends RuntimeException {
    public ImagesLimitExceededException(String message) {
        super(message);
    }
}
