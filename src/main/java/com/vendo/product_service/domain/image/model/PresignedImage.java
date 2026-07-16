package com.vendo.product_service.domain.image.model;

public record PresignedImage(
        String id,
        String uploadUrl,
        String key) {
}