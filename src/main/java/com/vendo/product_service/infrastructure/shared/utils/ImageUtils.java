package com.vendo.product_service.infrastructure.shared.utils;

import com.vendo.core_lib.utils.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class ImageUtils {

    private static final Set<String> IMAGE_MIME_TYPES = Set.of(
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

    private ImageUtils() {
    }

    public static boolean isValid(List<MultipartFile> files) {
        return files.stream().allMatch(ImageUtils::isValid);
    }

    public static boolean isValid(MultipartFile file) {
        return !isEmpty(file) && isImage(file);
    }

    private static boolean isEmpty(MultipartFile file) {
        return file == null || file.isEmpty();
    }

    private static boolean isImage(MultipartFile file) {
        String contentType = file.getContentType();
        return !StringUtils.isEmpty(contentType) && isImage(contentType);
    }

    private static boolean isImage(String contentType) {
        return contentType != null && IMAGE_MIME_TYPES.contains(contentType.toLowerCase(Locale.ROOT));
    }

}
