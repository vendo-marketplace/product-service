package com.vendo.product_service.adapter.image.out.aws.exception;

public class AwsServiceUnavailableException extends RuntimeException {
    public AwsServiceUnavailableException(String message) {
        super(message);
    }
}
