package com.vendo.product_service.adapter.aws.out.exception;

public class AwsServiceUnavailableException extends RuntimeException {
    public AwsServiceUnavailableException(String message) {
        super(message);
    }
}
