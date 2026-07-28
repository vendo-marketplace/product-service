package com.vendo.product_service.adapter.image.in.controller;

import com.vendo.core_lib.constants.Separators;
import com.vendo.core_lib.utils.StringUtils;
import com.vendo.product_service.domain.image.exception.EmptyImageException;
import com.vendo.product_service.domain.image.exception.NotImageException;
import com.vendo.product_service.domain.image.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

final class ImageValidator {

    private ImageValidator() {
    }

    public static void validate(List<MultipartFile> files) {
        files.forEach(ImageValidator::validate);
    }

    private static void validate(MultipartFile file) {
        throwIfEmpty(file);
        throwIfNotImage(file);
    }

    private static void throwIfEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EmptyImageException("%s is empty.".formatted(getDefaultFilename(file)));
        }
    }

    private static String getDefaultFilename(MultipartFile file) {
        if (file == null) return Image.getDefaultFilename(Separators.EMPTY_STRING);
        return Image.getDefaultFilename(file.getOriginalFilename());
    }

    private static void throwIfNotImage(MultipartFile file) {
        String contentType = file.getContentType(), filename = getDefaultFilename(file);

        if (StringUtils.isEmpty(contentType) || !Image.isImage(contentType)) {
            throw new NotImageException("%s has invalid image content type %s.".formatted(filename, contentType), contentType);
        }
    }

}
