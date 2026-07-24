package com.vendo.product_service.domain.image.model;

public record PresignImage(
        String id,
        String uploadUrl,
        String key) {
}