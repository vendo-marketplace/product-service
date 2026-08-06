package com.vendo.product_service.domain.image.model;

import lombok.Builder;
import lombok.With;

import java.util.List;

@Builder
public record Image(
        @With String id,
        byte[] bytes,
        String filename,
        String contentType,
        long size
) {

    private static final String DEFAULT_FILENAME = "File";

    public static Image findById(String id, List<Image> images) {
        return images.stream()
                .filter(image -> image.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Image not found by id: %s.".formatted(id)));
    }
}
