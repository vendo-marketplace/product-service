package com.vendo.product_service.adapter.product.in.controller;

import com.vendo.product_service.adapter.product.in.dto.CreateProductRequest;
import com.vendo.product_service.adapter.product.in.dto.ProductResponse;
import com.vendo.product_service.adapter.product.in.dto.UpdateProductRequest;
import com.vendo.product_service.adapter.product.out.mapper.DtoProductMapper;
import com.vendo.product_service.application.product.ProductService;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.product.ProductValidationPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@PreAuthorize("@userSecurity.validateActivation(authentication)")
public class ProductController {

    private final ProductService productService;
    private final ProductValidationPort validationPort;
    private final DtoProductMapper dtoProductMapper;

    @PostMapping
    public void save(@Valid @RequestBody CreateProductRequest request) {
        validationPort.validateOnSave(request);
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