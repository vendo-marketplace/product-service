package com.vendo.product_service.adapter.attribute.in.exception;

import com.vendo.product_service.domain.attribute.exception.AttributeNotFoundException;
import com.vendo.security_starter.response.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AttributeExceptionHandler {

    @ExceptionHandler(AttributeNotFoundException.class)
    ResponseEntity<ExceptionResponse> handleAttributeNotFoundException(AttributeNotFoundException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

}
