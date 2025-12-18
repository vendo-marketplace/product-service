package com.vendo.product_service.web;

import com.vendo.product_service.service.ProductService;
import com.vendo.product_service.web.dto.CreateProductRequest;
import com.vendo.product_service.web.dto.ProductResponse;
import com.vendo.product_service.web.dto.UpdateProductRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public void save(@Valid @RequestBody CreateProductRequest createProductRequest) {
        productService.save(createProductRequest);
    }

    @PutMapping("/{id}")
    public void update(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest updateProductRequest
    ) {
        productService.update(id, updateProductRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> find(@PathVariable String id) {
        return ResponseEntity.ok(productService.findById(id));
    }

}
