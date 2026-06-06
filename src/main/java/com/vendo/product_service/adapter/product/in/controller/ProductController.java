package com.vendo.product_service.adapter.product.in.controller;

import com.vendo.product_service.adapter.product.in.dto.CreateProductRequest;
import com.vendo.product_service.adapter.product.in.dto.ProductResponse;
import com.vendo.product_service.adapter.product.in.dto.UpdateProductRequest;
import com.vendo.product_service.adapter.product.out.mapper.DtoProductMapper;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.in.product.ProductUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@PreAuthorize("hasAuthority('ADMIN')")
class ProductController {

    private final ProductUseCase productUseCase;
    private final DtoProductMapper mapper;

    @PostMapping
    void save(@Valid @RequestBody CreateProductRequest request) {
        productUseCase.save(mapper.toEntity(request));
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