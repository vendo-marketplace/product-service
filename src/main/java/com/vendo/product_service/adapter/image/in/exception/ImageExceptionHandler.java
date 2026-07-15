package com.vendo.product_service.adapter.image.in.exception;

import com.vendo.product_service.domain.image.exception.InvalidImageException;
import com.vendo.security_lib.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ImageExceptionHandler {

    @ExceptionHandler(InvalidImageException.class)
    ResponseEntity<ExceptionResponse> handleInvalidImageException(InvalidImageException e, HttpServletRequest request) {
        // TODO handle errors
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("invalid message")
                .code(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

}
