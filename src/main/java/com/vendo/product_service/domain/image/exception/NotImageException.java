package com.vendo.product_service.domain.image.exception;

import com.vendo.core_lib.utils.StringUtils;
import lombok.Getter;

@Getter
public class NotImageException extends RuntimeException {

    private final String contentType;

    public NotImageException(String message, String contentType) {
        super(message);

        if (StringUtils.isEmpty(contentType)) {
            throw new IllegalArgumentException("Content type is empty.");
        }
        this.contentType = contentType;
    }

}
