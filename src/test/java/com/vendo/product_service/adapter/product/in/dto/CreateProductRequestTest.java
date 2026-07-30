package com.vendo.product_service.adapter.product.in.dto;

import com.vendo.product_service.domain.product.pattern.ProductPatterns;
import com.vendo.product_service.test_utils.builder.CreateProductRequestDataBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CreateProductRequestTest {

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    @Nested
    class TitleTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "iPhone",
                "iPhone 15",
                "iPhone 15 Pro",
                "Samsung Galaxy S25 Ultra",
                "MacBook Pro 16",
                "Lenovo ThinkPad X1 Carbon",
                "Dell XPS 13",
                "LG OLED C4 55",
                "Sony PlayStation 5",
                "Xbox Series X",
                "USB-C кабель",
                "USB 3.0 Flash Drive",
                "Wi-Fi 6 Router",
                "Телевізор LG 55",
                "Ноутбук Lenovo IdeaPad 5",
                "Кава 100% Arabica",
                "Стіл (дерево)",
                "Шафа-купе",
                "Кам'янець-Подільський",
                "Мар'їнка",
                "A4/A5 папір",
                "Bosch Serie 6",
                "Canon EOS R8",
                "HP EliteBook 840 G8",
                "BMW X5",
                "Audi A6 C8",
                "AT&T Router"
        })
        void title_shouldPassValidation_whenTitleValid(String title) {
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().title(title).build();
            passValidation(request);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "",
                " ",
                "  ",
                "\t",
                "\n",
                " iPhone",
                "-iPhone",
                "+iPhone",
                "/iPhone",
                ".iPhone",
                ",iPhone",
                "&iPhone",
                "%iPhone",
                ":iPhone",
                "\"iPhone",
                "'iPhone",
                "(iPhone",
                ")iPhone",
                "iPhone@",
                "iPhone#",
                "iPhone$",
                "iPhone!",
                "iPhone?",
                "iPhone*",
                "iPhone=",
                "iPhone<",
                "iPhone>",
                "iPhone|",
                "iPhone\\",
                "iPhone~",
                "iPhone^",
                "iPhone`",
                "iPhone;",
                "iPhone{",
                "iPhone}",
                "iPhone[",
                "iPhone]",
                "@iPhone",
                "#Samsung",
                "$100",
                "!Sale",
                "?Question",
                "iPhone----------------",
                "Samsung..............",
                "LG %%%%%%%%%%%%%%%%%",
                "\"\"\"\"\"\"\"\"",
                "(((((((((",
                ")))))))))",
                "&&&&&&&&",
                "++++++++",
                "////////",
                ",,,,,,,,",
                "::::::::",
                "<script>",
                "DROP TABLE",
                "SELECT * FROM users",
                "😀",
                "📱 iPhone",
                "Телефон😊",
                "iPhone🚀"
        })
        void title_shouldFailValidation_whenTitleIsInvalid(String title) {
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().title(title).build();
            failValidation(request, Set.of(ProductPatterns.PRODUCT_TITLE_VALIDATION_MESSAGE));
        }
    }

    @Nested
    class DescriptionTests {

        @ParameterizedTest
        @ValueSource(strings = {
                "Новий iPhone 15 Pro Max, пам'ять 256GB. Телефон у відмінному стані, повний комплект.",
                "Ноутбук Lenovo IdeaPad 5. Використовувався 6 місяців, без подряпин, батарея тримає до 8 годин.",
                "Продам телевізор LG 55\". Працює без нарікань, у комплекті пульт та коробка.",
                "Кавоварка De'Longhi у хорошому стані. Регулярно проходила очищення, всі функції працюють.",
                "Гірський велосипед Scott Aspect 960. Розмір рами M, пробіг близько 500 км.",
                "Дерев'яний письмовий стіл. Матеріал — дуб, розміри 120x60 см, є незначні сліди використання.",
                "Книга у відмінному стані. Обкладинка без пошкоджень, усі сторінки цілі.",
                "Новий USB-C кабель довжиною 2 м. Підтримує швидку зарядку до 100W.",
                "Пральна машина Bosch Serie 6. Повністю справна, продається у зв'язку з переїздом.",
                "Стан товару відповідає фотографіям. За додатковими питаннями пишіть у чат."
        })
        void description_shouldPassValidation(String description) {
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().description(description).build();
            passValidation(request);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {
                "    ",
                "\t",
                "\n",
                "abcd"
        })
        void description_shouldFailValidation(String description) {
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().description(description).build();
            failValidation(request, Set.of("Description must be between 5 and 250 characters.", "Description is required."));
        }

    }

    @Nested
    class AddressTests {

        static Stream<Arguments> inValidAddresses() {
            return Stream.of(
                    Arguments.of(
                    null, "Address is required."),
                    Arguments.of(
                        new AddressRequest(
                                "",
                                "Lviv region",
                                new AddressRequest.LocationRequest(49.8397, 24.0297)
                        ), "City should have from 2 to 100 characters."
                    ),
                    Arguments.of(
                            new AddressRequest(
                                    "a",
                                    "Lviv region",
                                    new AddressRequest.LocationRequest(49.8397, 24.0297)
                            ), "City should have from 2 to 100 characters."
                    ),
                    Arguments.of(
                        new AddressRequest(
                                "Lviv",
                                "Region is required.",
                                new AddressRequest.LocationRequest(49.8397, 24.0297)
                        ), "Region should have from 2 to 100 characters."
                    ),
                    Arguments.of(
                        new AddressRequest(
                                "Lviv",
                                "ab",
                                new AddressRequest.LocationRequest(49.8397, 24.0297)
                        ), "Region should have from 2 to 100 characters."
                    ),
                    Arguments.of(
                        new AddressRequest(
                                "Lviv",
                                "Lviv region",
                                null
                        ), "Location is required."
                    ),
                    Arguments.of(
                        new AddressRequest(
                                "Lviv",
                                "Lviv region",
                                new AddressRequest.LocationRequest(null, 24.0297)
                        ), "Latitude is required."
                    ),
                    Arguments.of(
                        new AddressRequest(
                                "Lviv",
                                "Lviv region",
                                new AddressRequest.LocationRequest(-91D, 24.0297)
                        ), "Minimal latitude should be -90."
                    ),
                    Arguments.of(
                        new AddressRequest(
                                "Lviv",
                                "Lviv region",
                                new AddressRequest.LocationRequest(91D, 24.0297)
                        ), "Maximum latitude should be 90."
                    ),
                    Arguments.of(
                            new AddressRequest(
                                    "Lviv",
                                    "Lviv region",
                                    new AddressRequest.LocationRequest(49.8397, null)
                            ), "Longitude is required."
                    ),
                    Arguments.of(
                        new AddressRequest(
                                "Lviv",
                                "Lviv region",
                                new AddressRequest.LocationRequest(49.8397, -181D)
                        ), "Minimal longitude should be -180."
                    ),
                    Arguments.of(
                        new AddressRequest(
                                "Lviv",
                                "Lviv region",
                                new AddressRequest.LocationRequest(49.8397, 181D)
                        ), "Maximum longitude should be 180."
                    )
            );
        }

        @ParameterizedTest
        @MethodSource("inValidAddresses")
        void address_shouldFailValidation(AddressRequest address, String message) {
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().address(address).build();
            failValidation(request, Set.of(message));
        }
    }

    @Nested
    class DetailsTests {

        @Test
        void quantity_shouldFailValidation_whenQuantityIsLessThanOne() {
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().quantity(0).build();
            failValidation(request, Set.of("Minimal quantity is one."));
        }

        @Test
        void isNew_shouldFailValidation_whenIsNewNotPresent() {
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().isNew(null).build();
            failValidation(request, Set.of("Is new flag is required."));
        }

        @Test
        void price_shouldFailValidation_whenPriceIsNull() {
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().price(null).build();
            failValidation(request, Set.of("Price is required."));
        }

        @Test
        void price_shouldFailValidation_whenPriceIsNegative() {
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().price(BigDecimal.valueOf(-1)).build();
            failValidation(request, Set.of("Price must be greater or equal to 0."));
        }

        @Test
        void price_shouldFailValidation_whenPriceFractionIsMoreThanTwo() {
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().price(BigDecimal.valueOf(100.234)).build();
            failValidation(request, Set.of("Price must have up to 8 digits before the decimal point and 2 after."));
        }

        @Test
        void category_shouldFailValidation_whenCategoryNotPresent() {
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().categoryId(null).build();
            failValidation(request, Set.of("Id is required."));
        }

        @Test
        void category_shouldFailValidation_whenCategoryIsBlank() {
            CreateProductRequest request = CreateProductRequestDataBuilder.withAllFields().categoryId("").build();
            failValidation(request, Set.of("Id is required."));
        }
    }

    private void passValidation(CreateProductRequest request) {
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<CreateProductRequest>> constraints = validator.validate(request);

        assertThat(constraints.size()).isEqualTo(0);
    }

    private void failValidation(CreateProductRequest request, Set<String> validationMessages) {
        Validator validator = validatorFactory.getValidator();

        Set<ConstraintViolation<CreateProductRequest>> constraints = validator.validate(request);
        Set<String> constraintMessages = constraints.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());

        assertThat(validationMessages.containsAll(constraintMessages)).isTrue();
    }
}
