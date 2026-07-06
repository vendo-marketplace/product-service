package com.vendo.product_service.adapter.attribute.in.dto;

import com.vendo.product_service.domain.attribute.model.AttributeType;
import com.vendo.product_service.domain.product.pattern.ProductPatterns;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CreateAttributeRequestTest {

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    private static CreateAttributeRequest.CreateAttributeRequestBuilder withRequiredFields() {
        return CreateAttributeRequest.builder()
                .title("Title")
                .slug("slug")
                .type(AttributeType.STRING);
    }

    @Nested
    class TitleTests {
        @Test
        void validate_shouldPassValidation() {
            CreateAttributeRequest request = withRequiredFields().title("Діагональ").build();
            passAttributeValidation(request);
        }

        @Test
        void validate_shouldPassValidation_whenTitleContainsApostrophe() {
            CreateAttributeRequest request = withRequiredFields().title("Оперативна пам'ять").build();
            passAttributeValidation(request);
        }

        @Test
        void validate_shouldReturnConstraint_whenTitleIsNotFromCapital() {
            Set<String> validationMessages = Set.of(ProductPatterns.TITLE_VALIDATION_MESSAGE);
            CreateAttributeRequest request = withRequiredFields().title("non capital title").build();
            failAttributeValidation(request, validationMessages);
        }

        @Test
        void validate_shouldReturnConstraint_whenTitleStartsWithNumber() {
            Set<String> validationMessages = Set.of(ProductPatterns.TITLE_VALIDATION_MESSAGE);
            CreateAttributeRequest request = withRequiredFields().title("1 Non capital title").build();
            failAttributeValidation(request, validationMessages);
        }

        @Test
        void validate_shouldReturnConstraint_whenTitleStartsWithSpecialSymbol() {
            Set<String> validationMessages = Set.of(ProductPatterns.TITLE_VALIDATION_MESSAGE);
            CreateAttributeRequest request = withRequiredFields().title("& Non capital title").build();
            failAttributeValidation(request, validationMessages);
        }

        @Test
        void validate_shouldReturnConstraint_whenTitleContainsUnderscore() {
            Set<String> validationMessages = Set.of(ProductPatterns.TITLE_VALIDATION_MESSAGE);
            CreateAttributeRequest request = withRequiredFields().title("Non_capital title").build();
            failAttributeValidation(request, validationMessages);
        }

        @Test
        void validate_shouldReturnConstraint_whenTitleContainsOnlyOneLetter() {
            Set<String> validationMessages = Set.of(ProductPatterns.TITLE_VALIDATION_MESSAGE);
            CreateAttributeRequest request = withRequiredFields().title("A").build();
            failAttributeValidation(request, validationMessages);
        }
    }

    @Nested
    class SlugTests {

        @Test
        void validate_shouldPassValidation() {
            CreateAttributeRequest request = withRequiredFields().slug("diagonal").build();
            passAttributeValidation(request);
        }

        @Test
        void validate_shouldPassValidation_whenSlugContainsUnderScores() {
            CreateAttributeRequest request = withRequiredFields().slug("curve_monitor_size").build();
            passAttributeValidation(request);
        }

        @Test
        void validate_shouldReturnConstraint_whenSlugIsNotInLatin() {
            Set<String> validationMessages = Set.of(ProductPatterns.SLUG_VALIDATION_MESSAGE);
            CreateAttributeRequest request = withRequiredFields().slug("бренд").build();
            failAttributeValidation(request, validationMessages);
        }

        @Test
        void validate_shouldReturnConstraint_whenSlugStartsWithCapital() {
            Set<String> validationMessages = Set.of(ProductPatterns.SLUG_VALIDATION_MESSAGE);
            CreateAttributeRequest request = withRequiredFields().slug("Brand").build();
            failAttributeValidation(request, validationMessages);
        }

        @Test
        void validate_shouldReturnConstraint_whenSlugContainsDash() {
            Set<String> validationMessages = Set.of(ProductPatterns.SLUG_VALIDATION_MESSAGE);
            CreateAttributeRequest request = withRequiredFields().slug("laptop-ram").build();
            failAttributeValidation(request, validationMessages);
        }

        @Test
        void validate_shouldReturnConstraint_whenSlugStartsWithUnderscore() {
            Set<String> validationMessages = Set.of(ProductPatterns.SLUG_VALIDATION_MESSAGE);
            CreateAttributeRequest request = withRequiredFields().slug("_laptop").build();
            failAttributeValidation(request, validationMessages);
        }

        @Test
        void validate_shouldReturnConstraint_whenSlugEndsWithUnderscore() {
            Set<String> validationMessages = Set.of(ProductPatterns.SLUG_VALIDATION_MESSAGE);
            CreateAttributeRequest request = withRequiredFields().slug("laptop_").build();
            failAttributeValidation(request, validationMessages);
        }

        @Test
        void validate_shouldReturnConstraint_whenSlugHasOnlyOneLetter() {
            Set<String> validationMessages = Set.of(ProductPatterns.SLUG_VALIDATION_MESSAGE);
            CreateAttributeRequest request = withRequiredFields().slug("a").build();
            failAttributeValidation(request, validationMessages);
        }

        @Test
        void validate_shouldReturnConstraint_whenSlugContainsWithNumber() {
            Set<String> validationMessages = Set.of(ProductPatterns.SLUG_VALIDATION_MESSAGE);
            CreateAttributeRequest request = withRequiredFields().slug("brand1").build();
            failAttributeValidation(request, validationMessages);
        }

    }


    private void passAttributeValidation(CreateAttributeRequest request) {
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<CreateAttributeRequest>> constraints = validator.validate(request);

        assertThat(constraints.size()).isEqualTo(0);
    }

    private void failAttributeValidation(CreateAttributeRequest request, Set<String> validationMessages) {
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<CreateAttributeRequest>> constraints = validator.validate(request);
        Set<String> constraintMessages = constraints.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());

        assertThat(validationMessages.containsAll(constraintMessages)).isTrue();
    }

}
