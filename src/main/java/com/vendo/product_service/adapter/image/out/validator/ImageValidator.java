package com.vendo.product_service.adapter.image.out.validator;

import com.vendo.core_lib.constants.Separators;
import com.vendo.product_service.domain.image.exception.EmptyImageException;
import com.vendo.product_service.domain.image.exception.InvalidImageException;
import com.vendo.product_service.domain.image.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public final class ImageValidator {

    private ImageValidator() {
    }

    public static List<Image> validate(List<MultipartFile> images) {
        return images.stream().map(ImageValidator::validate)
                .toList();
    }

    private static Image validate(MultipartFile file) {
        throwIfEmpty(file);

        try {
            Image image = Image.builder()
                    .size(file.getSize())
                    .contentType(file.getContentType())
                    .filename(file.getOriginalFilename())
                    .bytes(file.getBytes())
                    .build();

            image.throwIfNotImage();
            return image;
        } catch (IOException e) {
            throw new InvalidImageException("%s is invalid.".formatted(file.getOriginalFilename()));
        }
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
}
