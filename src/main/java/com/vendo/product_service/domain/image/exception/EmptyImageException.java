package com.vendo.product_service.domain.image.exception;

import lombok.Getter;

@Getter
public class EmptyImageException extends RuntimeException {
    public EmptyImageException(String message) {
        super(message);
    }
}
