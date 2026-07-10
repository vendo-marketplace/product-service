package com.vendo.product_service.domain.product_image.model;

import lombok.With;

public record ProductImage(
        String key,
        String contentType,
        long size,
        @With
        ImageStatus status
) {
}
