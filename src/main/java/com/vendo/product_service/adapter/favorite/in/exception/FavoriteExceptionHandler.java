package com.vendo.product_service.adapter.favorite.in.exception;

import com.vendo.product_service.domain.favorite.exception.FavoriteAlreadyExistsException;
import com.vendo.product_service.domain.favorite.exception.FavoriteNotFoundException;
import com.vendo.security_lib.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FavoriteExceptionHandler {

    @ExceptionHandler(FavoriteAlreadyExistsException.class)
    public ResponseEntity<ExceptionResponse> handleFavoriteAlreadyExistsException(FavoriteAlreadyExistsException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.CONFLICT.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionResponse);
    }

    @ExceptionHandler(FavoriteNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleFavoriteNotFoundException(FavoriteNotFoundException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

}