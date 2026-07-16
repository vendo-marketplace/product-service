package com.vendo.product_service.adapter.shared.out.http.exception;

public class HttpClientException extends RuntimeException {
    public HttpClientException(String message) {
        super(message);
    }
}
