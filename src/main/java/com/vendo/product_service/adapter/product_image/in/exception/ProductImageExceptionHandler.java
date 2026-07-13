package com.vendo.product_service.adapter.product_image.in.exception;

import com.vendo.product_service.domain.product_image.exception.ProductImageAlreadyExists;
import com.vendo.product_service.domain.product_image.exception.ProductImageNotFoundException;
import com.vendo.security_lib.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ProductImageExceptionHandler {

    @ExceptionHandler(ProductImageAlreadyExists.class)
    public ResponseEntity<ExceptionResponse> handleProductImageAlreadyExists(ProductImageAlreadyExists e, HttpServletRequest request) {
        log.info(e.getMessage());

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.CONFLICT.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionResponse);
    }

    @ExceptionHandler(ProductImageNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleProductImageNotFoundException(ProductImageNotFoundException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

}
