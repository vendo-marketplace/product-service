package com.vendo.product_service.adapter.spring.exception;

import com.vendo.product_service.shared.exception.formatter.FieldNormalizer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FieldErrorConverter {

    private final FieldNormalizer<String, String> fieldNormalizer;

    public Map<String, String> convertToMap(List<FieldError> fieldErrors) {
        return fieldErrors.stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldNormalizer.normalize(fieldError.getField()),
                        fieldError -> StringUtils.defaultIfEmpty(fieldError.getDefaultMessage(),
                                "No error message."))
                );
    }

}
