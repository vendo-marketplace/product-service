package com.vendo.product_service.domain.attribute.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class InvalidAttributesException extends RuntimeException {

    private final Map<String, String> errors;

    public InvalidAttributesException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

}
