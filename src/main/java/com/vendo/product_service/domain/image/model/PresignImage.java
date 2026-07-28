package com.vendo.product_service.domain.image.model;

import java.util.List;

public record PresignImage(
        String id,
        String uploadUrl,
        String key) {

    public static List<String> getKeys(List<PresignImage> images) {
        if (images == null) throw new IllegalArgumentException("Images are required.");
        return images.stream().map(PresignImage::key).toList();
    }

}