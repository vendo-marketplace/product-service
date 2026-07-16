package com.vendo.product_service.adapter.image.out.mapper;

import com.vendo.product_service.domain.image.exception.InvalidImageException;
import com.vendo.product_service.domain.image.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public final class ImageMapper {

    private ImageMapper() {
    }

    public static List<Image> toImages(List<MultipartFile> files) {
        return files.stream().map(ImageMapper::toImage)
                .toList();
    }

    private static Image toImage(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File is empty.");

        try {
            return Image.builder()
                    .size(file.getSize())
                    .contentType(file.getContentType())
                    .filename(file.getOriginalFilename())
                    .bytes(file.getBytes())
                    .build();
        } catch (IOException e) {
            throw new InvalidImageException("%s is invalid.".formatted(file.getOriginalFilename()));
        }
    }
}
