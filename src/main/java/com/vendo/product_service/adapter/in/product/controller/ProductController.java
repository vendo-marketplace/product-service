package com.vendo.product_service.adapter.in.product.controller;

import com.vendo.product_service.adapter.in.product.dto.CreateProductRequest;
import com.vendo.product_service.adapter.in.product.dto.ProductResponse;
import com.vendo.product_service.adapter.in.product.dto.UpdateProductRequest;
import com.vendo.product_service.adapter.in.product.mapper.ProductDtoMapper;
import com.vendo.product_service.application.ProductService;
import com.vendo.product_service.domain.category.validation.attribute.CategoryAttributeValidator;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.security.common.helper.SecurityContextHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ProductDtoMapper productDtoMapper;
    private final CategoryAttributeValidator categoryAttributeValidator;

    @PostMapping
    public void save(@Valid @RequestBody CreateProductRequest request) {

        categoryAttributeValidator.validateCategoryAttributes(
                request.categoryId(),
                request.attributes()
        );

        Product product = productDtoMapper.toEntity(request);
        product.setOwnerId(SecurityContextHelper.getUserIdFromContext());
        product.setActive(true);

        productService.save(product);
    }

    @PutMapping("/{id}")
    public void update(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request
    ) {
        Product product = productDtoMapper.toEntity(request);
        productService.update(id, product);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> find(@PathVariable String id) {

        Product product = productService.findById(id);
        return ResponseEntity.ok(productDtoMapper.toResponse(product));
    }
}