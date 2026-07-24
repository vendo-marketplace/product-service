package com.vendo.product_service.adapter;

import com.vendo.security_lib.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class TestExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, NullPointerException.class})
    protected ResponseEntity<ExceptionResponse> handleException(Exception e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder().message("Internal server error.").code(HttpStatus.INTERNAL_SERVER_ERROR.value()).path(request.getRequestURI()).build();
        return ResponseEntity.internalServerError().body(exceptionResponse);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<ExceptionResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, HttpServletRequest request) {
        log.info(String.valueOf(e));
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("File size upload exceeded.")
                .code(HttpStatus.BAD_REQUEST.value())
                .errors(Map.of(e.getBody().getTitle(), e.getBody().getDetail()))
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.internalServerError().body(exceptionResponse);
    }


}
