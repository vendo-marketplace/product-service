package com.vendo.product_service.domain.image.model;

public record Image(
        String id,
        byte[] bytes,
        String contentType,
        long size
) {
}
