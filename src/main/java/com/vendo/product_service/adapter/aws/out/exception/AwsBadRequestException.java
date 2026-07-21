package com.vendo.product_service.adapter.aws.out.exception;

import com.vendo.security_lib.exception.ExceptionResponse;
import lombok.Getter;

@Getter
public class AwsBadRequestException extends RuntimeException {

    private ExceptionResponse exceptionResponse;

    public AwsBadRequestException(String message) {
        super(message);
    }

    public AwsBadRequestException(ExceptionResponse exceptionResponse) {
        this.exceptionResponse = exceptionResponse;
    }
}
