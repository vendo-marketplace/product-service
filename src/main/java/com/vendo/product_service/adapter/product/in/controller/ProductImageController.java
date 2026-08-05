package com.vendo.product_service.adapter.product.in.controller;

import com.vendo.product_service.adapter.image.out.mapper.ImageMapper;
import com.vendo.product_service.infrastructure.shared.annotation.ImageFile;
import com.vendo.product_service.port.product.usecase.ProductImageUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/products/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageUseCase productImageUseCase;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(
            @NotBlank(message = "Id is required.")
            @RequestParam String id,

            @Valid
            @ImageFile
            @NotEmpty(message = "Images are required.")
            @RequestParam List<MultipartFile> images
    ) {
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
