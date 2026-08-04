package com.vendo.product_service.adapter.product.in.controller;

import com.vendo.product_service.adapter.image.out.mapper.ImageMapper;
import com.vendo.product_service.infrastructure.shared.validator.ImageValidator;
import com.vendo.product_service.port.product.usecase.ProductImageUseCase;
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
@RequestMapping("/products/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageUseCase productImageUseCase;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(
            @NotBlank(message = "Id is required.")
            @RequestParam String id,

            @NotEmpty(message = "Images are required.")
            @RequestParam List<MultipartFile> images
    ) {
        ImageValidator.validate(images);
        productImageUseCase.upload(id, ImageMapper.toImages(images));
    }

    @DeleteMapping
    public void delete(
            @NotBlank(message = "Id is required.")
            @RequestParam String id,

            @NotBlank(message = "Image key is required.")
            @RequestParam String imageKey
    ) {
        productImageUseCase.delete(id, imageKey);
    }
}
