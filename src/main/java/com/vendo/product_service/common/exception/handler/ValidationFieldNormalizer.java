package com.vendo.product_service.common.exception.handler;

import org.springframework.stereotype.Service;

@Service
public class ValidationFieldNormalizer implements FieldNormalizer<String, String> {

    private static final String ARRAY_BRACKET_IN = "[";

    private static final String ARRAY_BRACKET_OUT = "]";

    @Override
    public String normalize(String field) {
        if (isNestedValidation(field)) {
            field = retrieveNestedField(field);
        }
        return field;
    }

    private static boolean isNestedValidation(String validationField) {
        return validationField.contains(ARRAY_BRACKET_IN) && validationField.contains(ARRAY_BRACKET_OUT);
    }

    private static String retrieveNestedField(String field) throws StringIndexOutOfBoundsException {
        int start = field.lastIndexOf(ARRAY_BRACKET_IN);
        int end = field.indexOf(ARRAY_BRACKET_OUT);

        if (start < 0 || end < 0 || start >= end) {
            return field;
        }

        return field.substring(start + 1, end);
    }
}
