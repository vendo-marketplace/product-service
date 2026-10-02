package com.vendo.product_service.adapter.product.in.controller;

import com.vendo.product_service.adapter.image.out.mapper.ImageMapper;
import com.vendo.product_service.adapter.product.in.dto.CreateProductRequest;
import com.vendo.product_service.adapter.product.in.dto.ProductResponse;
import com.vendo.product_service.adapter.product.in.dto.UpdateProductRequest;
import com.vendo.product_service.adapter.product.out.mapper.DtoProductMapper;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.infrastructure.shared.annotation.ImageFile;
import com.vendo.product_service.port.product.usecase.ProductUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
class ProductController {

    private final ProductUseCase productUseCase;
    private final DtoProductMapper mapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void save(
            @Valid @RequestPart CreateProductRequest request,

            @Valid
            @ImageFile
            @NotEmpty(message = "Images are required.")
            @RequestPart List<MultipartFile> images
    ) {
        productUseCase.save(
                ImageMapper.toImages(images),
                mapper.toEntity(request)
        );
    }

    @PutMapping("/{id}")
    void update(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        productUseCase.update(id, mapper.toEntity(request));
    }

    @GetMapping("/{id}")
    ResponseEntity<ProductResponse> find(@PathVariable String id) {
        Product product = productUseCase.findById(id);
        return ResponseEntity.ok(mapper.toResponse(product));
    }
}