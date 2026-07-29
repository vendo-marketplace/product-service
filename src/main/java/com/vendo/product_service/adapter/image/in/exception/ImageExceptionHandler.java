package com.vendo.product_service.adapter.image.in.exception;

import com.vendo.core_lib.utils.ClassFields;
import com.vendo.product_service.domain.image.exception.*;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.security_lib.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ImageExceptionHandler {

    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidImageException(InvalidImageException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(NotImageException.class)
    public ResponseEntity<ExceptionResponse> handleNotImageException(NotImageException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Image validation failed.")
                .errors(Map.of(ClassFields.nameOf("contentType", Image.class), e.getMessage()))
                .code(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(EmptyImageException.class)
    public ResponseEntity<ExceptionResponse> handleEmptyImageException(EmptyImageException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Image validation failed.")
                .errors(Map.of(ClassFields.nameOf("size", Image.class), e.getMessage()))
                .code(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(ImagesLimitExceededException.class)
    public ResponseEntity<ExceptionResponse> handleImageExceedSizeException(ImagesLimitExceededException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(ImageKeyNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleImageKeyNotFoundException(ImageKeyNotFoundException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionResponse);
    }

    @ExceptionHandler(ImageUploadException.class)
    public ResponseEntity<ExceptionResponse> handleImageUploadException(ImageUploadException e, HttpServletRequest request) {
        log.error("Image upload internal error occurred: {}.", e.getMessage());
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Internal server error.")
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

}
