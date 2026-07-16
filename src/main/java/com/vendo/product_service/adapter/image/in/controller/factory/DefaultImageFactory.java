package com.vendo.product_service.adapter.image.in.controller.factory;

import com.vendo.product_service.domain.image.exception.InvalidImageException;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.port.IdGenerationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultImageFactory implements ImageFactory {

    private final IdGenerationPort idGenerationPort;

    @Override
    public List<Image> create(List<MultipartFile> images) {
        return images.stream().map(this::create).toList();
    }

    private Image create(MultipartFile file) {
        try {
            return new Image(idGenerationPort.generate(), file.getBytes(), file.getContentType(), file.getSize());
        } catch (IOException e) {
            String filename = file.getOriginalFilename() == null ? "Image" : file.getOriginalFilename();
            throw new InvalidImageException("%s is invalid.".formatted(filename));
        }
    }
}
