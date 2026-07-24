package com.vendo.product_service.domain.product.pattern;

public final class ProductPatterns {

    public static final String TITLE_PATTERN = "^[A-ZА-ЯІЇЄ][a-zа-яіїє]+(?:['’][A-Za-zА-ЯІЇЄа-яіїє]+)*(?: [A-Za-zА-ЯІЇЄа-яіїє][a-zа-яіїє]*(?:['’][A-Za-zА-ЯІЇЄа-яіїє]+)*)?$";
    public static final String TITLE_VALIDATION_MESSAGE = "Title must start with a capital letter and may contain only letters, spaces, hyphens, commas, and apostrophes.";

    public static final String SLUG_PATTERN = "[a-z0-9]+(?:_[a-z0-9]+)*$";
    public static final String SLUG_VALIDATION_MESSAGE = "Slug may contain only lowercase letters, numbers, and underscores, and cannot start or end with an underscore.";

    private ProductPatterns() {}

}

