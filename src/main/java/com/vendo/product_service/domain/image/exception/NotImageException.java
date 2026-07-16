package com.vendo.product_service.domain.image.exception;

import lombok.Getter;

@Getter
public class NotImageException extends RuntimeException {

    private final String contentType;

    public NotImageException(String message, String contentType) {
        super(message);

        if (contentType == null || contentType.isBlank()) {
            throw new IllegalArgumentException("Content type is empty.");
        }
        this.contentType = contentType;
    }

}
