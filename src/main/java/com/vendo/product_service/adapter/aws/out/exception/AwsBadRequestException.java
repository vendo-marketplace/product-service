package com.vendo.product_service.adapter.aws.out.exception;

import com.vendo.security_lib.exception.ExceptionResponse;
import lombok.Getter;

@Getter
public class AwsBadRequestException extends RuntimeException {

    private final ExceptionResponse exceptionResponse;

    public AwsBadRequestException(ExceptionResponse exceptionResponse) {
        this.exceptionResponse = exceptionResponse;
    }
}
