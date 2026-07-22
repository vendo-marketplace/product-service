package com.vendo.product_service.domain.image.model;

import com.vendo.core_lib.utils.StringUtils;
import com.vendo.product_service.domain.image.exception.ImageExceedSizeException;
import com.vendo.product_service.domain.image.exception.NotImageException;
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
    private static final int MEGABYTE_IN_BYTES = 1_048_576;

    public static Image findById(String id, List<Image> images) {
        return images.stream()
                .filter(image -> image.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Image not found by id: %s.".formatted(id)));
    }

    public static String getDefaultFilename(String filename) {
        return StringUtils.defaultIfEmpty(filename, DEFAULT_FILENAME);
    }

    public void validate(int maxSize) {
        String defaultFilename = getDefaultFilename(filename);

        if (isSizeExceeded(maxSize)) {
            throw new ImageExceedSizeException("%s is too large. Maximum size is %d.".formatted(defaultFilename, maxSize / MEGABYTE_IN_BYTES), size);
        }

        if (!isImage()) {
            throw new NotImageException("%s has invalid image content type %s.".formatted(defaultFilename, contentType), contentType);
        }
    }

    private boolean isSizeExceeded(int maxSize) {
        return size > maxSize;
    }

    private boolean isImage() {
        return contentType != null && IMAGE_MIME_TYPES.contains(contentType.toLowerCase(Locale.ROOT));
    }
}
