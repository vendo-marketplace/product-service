package com.vendo.product_service.adapter.attribute.in.exception;

import com.vendo.product_service.domain.attribute.exception.AttributeAlreadyExistsException;
import com.vendo.product_service.domain.attribute.exception.AttributeNotFoundException;
import com.vendo.product_service.domain.attribute.exception.InvalidAttributesException;
import com.vendo.security_lib.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AttributeExceptionHandler {

    @ExceptionHandler(AttributeNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleAttributeNotFoundException(AttributeNotFoundException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(AttributeAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleAttributeAlreadyExistsException(AttributeAlreadyExistsException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.CONFLICT.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionResponse);
    }

    @ExceptionHandler(InvalidAttributesException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidAttributesException(InvalidAttributesException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .errors(e.getErrors())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }


}
