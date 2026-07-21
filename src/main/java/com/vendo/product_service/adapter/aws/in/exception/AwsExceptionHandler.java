package com.vendo.product_service.adapter.aws.in.exception;

import com.vendo.product_service.adapter.aws.out.exception.AwsInternalServerException;
import com.vendo.product_service.adapter.aws.out.exception.AwsServiceUnavailableException;
import com.vendo.security_lib.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AwsExceptionHandler {

    @ExceptionHandler(AwsServiceUnavailableException.class)
    public ResponseEntity<ExceptionResponse> handleAwsServiceUnavailableException(AwsServiceUnavailableException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.SERVICE_UNAVAILABLE.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(exceptionResponse);
    }

    @ExceptionHandler(AwsInternalServerException.class)
    public ResponseEntity<ExceptionResponse> handleAwsInternalServerException(AwsInternalServerException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

}
