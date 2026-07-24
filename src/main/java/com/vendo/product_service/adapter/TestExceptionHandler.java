package com.vendo.product_service.adapter;

import com.vendo.security_lib.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class TestExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, NullPointerException.class})
    protected ResponseEntity<ExceptionResponse> handleException(Exception e, HttpServletRequest request) {
        log.error(String.valueOf(e));
        log.error("Handling internal exception: {}.", e.getMessage());
        log.error(String.valueOf(e.getCause()));
        log.error(String.valueOf(e.getCause().getClass()));
        log.error(e.getClass().getName());
        ExceptionResponse exceptionResponse = ExceptionResponse.builder().message("Internal server error.").code(HttpStatus.INTERNAL_SERVER_ERROR.value()).path(request.getRequestURI()).build();
        return ResponseEntity.internalServerError().body(exceptionResponse);
    }


}
