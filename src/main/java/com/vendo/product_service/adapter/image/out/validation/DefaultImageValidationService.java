package com.vendo.product_service.adapter.image.out.validation;

import com.vendo.product_service.adapter.image.in.controller.validation.ImageValidationService;
import com.vendo.product_service.domain.image.exception.InvalidImageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class DefaultImageValidationService implements ImageValidationService {

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

    @Value("${product.image.max-size}")
    private int IMAGE_MAX_SIZE;

    @Override
    public void validate(List<MultipartFile> images) {
        images.forEach(this::validate);
    }

    private void validate(MultipartFile image) {
        String filename = getFilename(image.getOriginalFilename());
        throwIfEmpty(image, filename);
        throwIfSizeExceeded(image.getSize(), filename);
        throwIfInvalidContentType(image.getContentType(), filename);
    }

    private void throwIfEmpty(MultipartFile image, String filename) {
        if (image.isEmpty()) {
            throw new InvalidImageException("%s is empty.".formatted(filename));
        }
    }

    private void throwIfSizeExceeded(long size, String filename) {
        if (size > IMAGE_MAX_SIZE) {
            throw new InvalidImageException("%s is to large. Maximum size is %d.".formatted(filename, IMAGE_MAX_SIZE / MEGABYTE_IN_BYTES));
        }
    }

    private void throwIfInvalidContentType(String contentType, String filename) {
        if (!isImage(contentType)) {
            throw new InvalidImageException("%s has invalid image content type.".formatted(filename));
        }
    }

    private String getFilename(String originalFilename) {
        return originalFilename == null ? DEFAULT_FILENAME : originalFilename;
    }

    private boolean isImage(String contentType) {
        return contentType != null && IMAGE_MIME_TYPES.contains(contentType.toLowerCase(Locale.ROOT));
    }
}
