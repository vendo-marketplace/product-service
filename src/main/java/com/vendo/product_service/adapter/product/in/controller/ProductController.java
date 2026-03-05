package com.vendo.product_service.adapter.product.in.controller;

import com.vendo.product_service.adapter.product.in.dto.CreateProductRequest;
import com.vendo.product_service.adapter.product.in.dto.ProductResponse;
import com.vendo.product_service.adapter.product.in.dto.UpdateProductRequest;
import com.vendo.product_service.adapter.product.out.mapper.DtoProductMapper;
import com.vendo.product_service.application.product.ProductService;
import com.vendo.product_service.application.category.validation.attribute.AttributesValidator;
import com.vendo.product_service.domain.product.model.Product;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final DtoProductMapper dtoProductMapper;
    private final AttributesValidator attributesValidator;

    @PostMapping
    public void save(@Valid @RequestBody CreateProductRequest request) {
        attributesValidator.validateAttributes(
                request.categoryId(),
                request.attributes()
        );

        Product product = dtoProductMapper.toEntity(request);
        productService.save(product);
    }

    @PutMapping("/{id}")
    public void update(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        Product product = dtoProductMapper.toEntity(request);
        productService.update(id, product);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> find(@PathVariable String id) {

        Product product = productService.findById(id);
        return ResponseEntity.ok(dtoProductMapper.toResponse(product));
    }
}