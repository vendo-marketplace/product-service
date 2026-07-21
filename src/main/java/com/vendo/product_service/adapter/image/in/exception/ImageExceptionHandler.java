package com.vendo.product_service.adapter.image.in.exception;

import com.vendo.core_lib.utils.ClassFields;
import com.vendo.product_service.domain.image.exception.ImageExceedSizeException;
import com.vendo.product_service.domain.image.exception.ImageUploadException;
import com.vendo.product_service.domain.image.exception.InvalidImageException;
import com.vendo.product_service.domain.image.exception.NotImageException;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.security_lib.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ImageExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        Map<String, String> errors = e.getConstraintViolations().stream()
                .collect(Collectors.toMap(this::lastPropertyNode, ConstraintViolation::getMessage, (first, second) -> first));

        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Validation failed.")
                .errors(errors)
                .code(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(InvalidImageException.class)
    ResponseEntity<ExceptionResponse> handleInvalidImageException(InvalidImageException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message(e.getMessage())
                .code(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(NotImageException.class)
    ResponseEntity<ExceptionResponse> handleNotImageException(NotImageException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Validation failed.")
                .errors(Map.of(ClassFields.nameOf("contentType", Image.class), e.getMessage()))
                .code(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(ImageExceedSizeException.class)
    ResponseEntity<ExceptionResponse> handleImageExceedSizeException(ImageExceedSizeException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Validation failed.")
                .errors(Map.of(ClassFields.nameOf("size", Image.class), e.getMessage()))
                .code(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionResponse);
    }

    @ExceptionHandler(ImageUploadException.class)
    ResponseEntity<ExceptionResponse> handleImageUploadException(ImageUploadException e, HttpServletRequest request) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .message("Image upload internal error.")
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
    }

    private String lastPropertyNode(ConstraintViolation<?> violation) {
        String name = "";
        for (Path.Node node : violation.getPropertyPath()) {
            name = node.getName();
        }
        return name;
    }

}
