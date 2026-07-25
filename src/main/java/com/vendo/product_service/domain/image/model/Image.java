package com.vendo.product_service.domain.image.model;

import com.vendo.core_lib.utils.StringUtils;
import lombok.Builder;
import lombok.With;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Builder
public record Image(
        @With String id,
        byte[] bytes,
        String filename,
        String contentType,
        long size
) {

    public static final Set<String> IMAGE_MIME_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp",
            "image/bmp",
            "image/tiff",
            "image/svg+xml",
            "image/x-icon",
            "image/heic",
            "image/heif",
            "image/avif",
            "image/apng"
    );

    private static final String DEFAULT_FILENAME = "File";

    public static Image findById(String id, List<Image> images) {
        return images.stream()
                .filter(image -> image.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Image not found by id: %s.".formatted(id)));
    }

    public static String getDefaultFilename(String filename) {
        return StringUtils.defaultIfEmpty(filename, DEFAULT_FILENAME);
    }

    public static boolean isImage(String contentType) {
        return contentType != null && IMAGE_MIME_TYPES.contains(contentType.toLowerCase(Locale.ROOT));
    }
}
