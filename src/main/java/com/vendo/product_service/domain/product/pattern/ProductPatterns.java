package com.vendo.product_service.domain.product.pattern;

public final class ProductPatterns {

    public static final String CATEGORY_TITLE_PATTERN = "^[A-ZА-ЯІЇЄ][A-Za-zА-Яа-яІіЇїЄє0-9'’/,-]*(?: [A-Za-zА-Яа-яІіЇїЄє0-9'’/,-]+)*$";
    public static final String CATEGORY_TITLE_VALIDATION_MESSAGE = "Title must start with a capital letter and may contain only letters, spaces, commas, slashes, hyphens, and apostrophes.";

    public static final String ATTRIBUTE_TITLE_PATTERN = "^[A-ZА-ЯІЇЄ][A-Za-zА-Яа-яІіЇїЄє0-9'’/,-]*(?: [A-Za-zА-Яа-яІіЇїЄє0-9'’/,-]+)*$";
    public static final String ATTRIBUTE_TITLE_VALIDATION_MESSAGE = "Title must start with a capital letter and may contain only letters, spaces, commas, slashes, hyphens, and apostrophes.";

    public static final String PRODUCT_TITLE_PATTERN = "^[\\p{L}0-9][\\p{L}0-9\\s'’.,()\\-+/&\":%]*$";
    public static final String PRODUCT_TITLE_VALIDATION_MESSAGE = "Title may contain only letters, numbers, spaces, and common punctuation characters.";

    public static final String SLUG_PATTERN = "[a-z0-9]+(?:_[a-z0-9]+)*$";
    public static final String SLUG_VALIDATION_MESSAGE = "Slug may contain only lowercase letters, numbers, and underscores, and cannot start or end with an underscore.";

    private ProductPatterns() {}

}

