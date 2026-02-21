package com.vendo.product_service.domain.category.validation.attribute;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryConstants {

    public static final String CATEGORY_ATTRIBUTE_NAME_PATTERN = "^[A-ZА-ЯІЇЄ][a-zа-яіїє]*(?:(?:[ -][A-Za-zА-ЯІЇЄа\\-zа-яіїє]+)|(?:, [A-Za-zА-ЯІЇЄa-zа-яіїє]+))*$";

}
