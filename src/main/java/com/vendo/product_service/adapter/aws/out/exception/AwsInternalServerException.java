package com.vendo.product_service.adapter.aws.out.exception;

public class AwsInternalServerException extends RuntimeException {
    public AwsInternalServerException(String message) {
        super(message);
    }
}
