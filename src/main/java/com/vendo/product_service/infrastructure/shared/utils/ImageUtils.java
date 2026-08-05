package com.vendo.product_service.infrastructure.shared.utils;

import com.vendo.core_lib.utils.StringUtils;
import com.vendo.product_service.domain.image.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public final class ImageUtils {

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
        return !StringUtils.isEmpty(contentType) && Image.isImage(contentType);
    }

}
