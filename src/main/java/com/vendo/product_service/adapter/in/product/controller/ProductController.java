package com.vendo.product_service.adapter.in.product.controller;

import com.vendo.product_service.adapter.in.product.adapter.ProductAdapter;
import com.vendo.product_service.adapter.in.product.dto.CreateProductRequest;
import com.vendo.product_service.adapter.in.product.dto.ProductResponse;
import com.vendo.product_service.adapter.in.product.dto.UpdateProductRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductAdapter productAdapter;

    @PostMapping
    public void save(@Valid @RequestBody CreateProductRequest createProductRequest) {
        productAdapter.save(createProductRequest);
    }

    @PutMapping("/{id}")
    public void update(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest updateProductRequest
    ) {
        productAdapter.update(id, updateProductRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> find(@PathVariable String id) {
        return ResponseEntity.ok(productAdapter.findById(id));
    }

}
