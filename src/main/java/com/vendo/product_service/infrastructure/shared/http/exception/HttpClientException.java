package com.vendo.product_service.infrastructure.shared.http.exception;

public class HttpClientException extends RuntimeException {
    public HttpClientException(String message) {
        super(message);
    }
}
