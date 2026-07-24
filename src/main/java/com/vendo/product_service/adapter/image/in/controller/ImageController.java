package com.vendo.product_service.adapter.image.in.controller;

import com.vendo.product_service.adapter.image.out.mapper.ImageMapper;
import com.vendo.product_service.port.image.ImageUseCase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageUseCase imageUseCase;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(
            @NotBlank(message = "Product ID is required.")
            @RequestParam String productId,

            @NotEmpty(message = "Images are required.")
            @RequestParam List<MultipartFile> images
    ) {
        imageUseCase.upload(productId, ImageMapper.toImages(images));
    }
}
