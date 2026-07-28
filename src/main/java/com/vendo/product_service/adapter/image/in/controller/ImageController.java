package com.vendo.product_service.adapter.image.in.controller;

import com.vendo.product_service.adapter.image.out.mapper.ImageMapper;
import com.vendo.product_service.port.image.usecase.ImageUseCase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageUseCase imageUseCase;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(
            @NotBlank(message = "Product ID is required.")
            @RequestParam String productId,

            @NotEmpty(message = "Images are required.")
            @RequestParam List<MultipartFile> images
    ) {
        ImageValidator.validate(images);
        imageUseCase.upload(productId, ImageMapper.toImages(images));
    }

    @DeleteMapping
    public void delete(
            @NotBlank(message = "Product ID is required.")
            @RequestParam String productId,

            @NotBlank(message = "Image key is required.")
            @RequestParam String imageKey
    ) {
        imageUseCase.delete(productId, imageKey);
    }
}
