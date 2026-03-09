package com.vendo.product_service.adapter.security.in.exception;

import com.vendo.core_lib.exception.ExceptionResponse;
import com.vendo.security_lib.exception.AccessDeniedException;
import com.vendo.security_lib.exception.InvalidTokenException;
import com.vendo.user_lib.exception.UserBlockedException;
import com.vendo.user_lib.exception.UserEmailNotVerifiedException;
import com.vendo.user_lib.exception.UserIsUnactiveException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.FORBIDDEN.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidTokenException(InvalidTokenException e, HttpServletRequest request) {
        log.error(e.getMessage());

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Invalid token.")
                .code(HttpStatus.UNAUTHORIZED.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> handleExpiredJwtException(ExpiredJwtException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Token has expired.")
                .code(HttpStatus.UNAUTHORIZED.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ExceptionResponse> handleJwtException(JwtException e,  HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Invalid token.")
                .code(HttpStatus.UNAUTHORIZED.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exceptionResponse);
    }

    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<ExceptionResponse> handleUserBlockedException(UserBlockedException e,  HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.FORBIDDEN.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }

    @ExceptionHandler(UserIsUnactiveException.class)
    public ResponseEntity<ExceptionResponse> handleUserIsUnactiveException(UserIsUnactiveException e,  HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.FORBIDDEN.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }

    @ExceptionHandler(UserEmailNotVerifiedException.class)
    public ResponseEntity<ExceptionResponse> handleUserEmailNotVerifiedException(UserEmailNotVerifiedException e,  HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.FORBIDDEN.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(exceptionResponse);
    }
}
