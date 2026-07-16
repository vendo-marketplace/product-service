package com.vendo.product_service.domain.image.exception;

import lombok.Getter;

@Getter
public class ImageExceedSizeException extends RuntimeException {

    private final long size;

    public ImageExceedSizeException(String message, long size) {
        super(message);
        this.size = size;
    }
}
