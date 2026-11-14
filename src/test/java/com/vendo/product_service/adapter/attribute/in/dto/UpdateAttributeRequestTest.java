package com.vendo.product_service.adapter.attribute.in.dto;

import com.vendo.product_service.domain.product.pattern.ProductPatterns;
import com.vendo.product_service.test_utils.builder.UpdateAttributeRequestDataBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateAttributeRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validate_shouldPassValidation_whenAllFieldsValid() {
        UpdateAttributeRequest request = UpdateAttributeRequestDataBuilder.withAllFields().build();
        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void validate_shouldReturnConstraints_whenTitleAndSlugAreNull() {
        UpdateAttributeRequest request = UpdateAttributeRequestDataBuilder.withAllFields().title(null).slug(null).build();
        assertThat(messages(request)).containsExactlyInAnyOrder("Title is required.", "Slug is required.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"non capital title", "1 Title", "Non_capital title"})
    void validate_shouldReturnConstraint_whenTitleIsInvalid(String title) {
        UpdateAttributeRequest request = UpdateAttributeRequestDataBuilder.withAllFields().title(title).build();
        assertThat(messages(request)).containsExactly(ProductPatterns.ATTRIBUTE_TITLE_VALIDATION_MESSAGE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Brand", "laptop-ram", "_laptop", "бренд"})
    void validate_shouldReturnConstraint_whenSlugIsInvalid(String slug) {
        UpdateAttributeRequest request = UpdateAttributeRequestDataBuilder.withAllFields().slug(slug).build();
        assertThat(messages(request)).containsExactly(ProductPatterns.SLUG_VALIDATION_MESSAGE);
    }

    private Set<String> messages(UpdateAttributeRequest request) {
        return validator.validate(request).stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
    }
}
