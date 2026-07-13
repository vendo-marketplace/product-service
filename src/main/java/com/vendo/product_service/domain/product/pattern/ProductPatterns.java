package com.vendo.product_service.domain.product.pattern;

public final class ProductPatterns {

    public static final String TITLE_PATTERN = "^[A-ZА-ЯІЇЄ][a-zа-яіїє]+(?:['’][A-Za-zА-ЯІЇЄа-яіїє]+)*(?: [A-Za-zА-ЯІЇЄа-яіїє][a-zа-яіїє]*(?:['’][A-Za-zА-ЯІЇЄа-яіїє]+)*)?$";
    public static final String TITLE_VALIDATION_MESSAGE = "Title must start with a capital letter and may contain only letters, spaces, hyphens, commas, and apostrophes.";

    public static final String SLUG_PATTERN = "[a-z0-9]+(?:_[a-z0-9]+)*$";
    public static final String SLUG_VALIDATION_MESSAGE = "Slug may contain only lowercase letters, numbers, and underscores, and cannot start or end with an underscore.";

    public static final String PHOTO_IMAGE_KEY_PATTERN = "^[a-z]+/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(?:png|jpe?g|webp|bmp|avif|svg|tiff|ico|heic|heif|apng)$";
    public static final String PHOTO_IMAGE_KEY_MESSAGE = "Image key must match the format '<folder>/<uuid>.<image-extension>'.";

    private ProductPatterns() {}

}

