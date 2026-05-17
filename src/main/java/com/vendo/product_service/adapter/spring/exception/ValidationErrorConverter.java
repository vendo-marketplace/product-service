package com.vendo.product_service.adapter.spring.exception;

import com.vendo.product_service.shared.exception.formatter.FieldNormalizer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ValidationErrorConverter {

    private final FieldNormalizer<String, String> fieldNormalizer;

    public Map<String, String> fromField(List<FieldError> fieldErrors) {
        return fieldErrors.stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldNormalizer.normalize(fieldError.getField()),
                        fieldError -> StringUtils.defaultIfEmpty(fieldError.getDefaultMessage(),
                                "No error message."))
                );
    }

    public Map<String, String> fromParameter(List<ParameterValidationResult> parameterErrors) {
        Map<String, String> errors = new TreeMap<>();

        for (ParameterValidationResult error : parameterErrors) {
            String parameterName = error.getMethodParameter().getParameterName();

            String message = error.getResolvableErrors()
                    .stream()
                    .findFirst()
                    .map(MessageSourceResolvable::getDefaultMessage)
                    .orElse("No error message.");

            errors.put(parameterName, message);
        }

        return errors;
    }
}
