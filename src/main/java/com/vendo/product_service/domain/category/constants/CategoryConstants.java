package com.vendo.product_service.domain.category.constants;

public class CategoryConstants {

    public static final String CATEGORY_ATTRIBUTE_NAME_PATTERN = "^[A-ZА-ЯІЇЄ][a-zа-яіїє]*(?:(?:[ -][A-Za-zА-ЯІЇЄа\\-zа-яіїє]+)|(?:, [A-Za-zА-ЯІЇЄa-zа-яіїє]+))*$";
    public static final String CATEGORY_TITLE_PATTERN = "^[A-ZА-ЯІЇЄ][A-Za-zА-ЯІЇЄа-яіїє]*(?:[ -][A-Za-zА-ЯІЇЄа-яіїє]+)*$";
    public static final String CATEGORY_CODE_PATTERN = "^[a-z0-9]+(?:-[a-z0-9]+)*$";

    private CategoryConstants() {
    }
}
