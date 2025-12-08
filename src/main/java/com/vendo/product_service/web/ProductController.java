package com.vendo.product_service.web;

import com.vendo.product_service.common.mapper.ProductMapper;
import com.vendo.product_service.service.ProductService;
import com.vendo.product_service.web.dto.CreateProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController("/products")
public class ProductController {

    private final ProductService productService;

    private final ProductMapper productMapper;

    @PostMapping
    public void save(@RequestBody CreateProductRequest createProductRequest) {
        productService.save(productMapper.toProduct(createProductRequest));
    }

    @PutMapping
    public void update() {

    }

    @GetMapping
    public void find() {

    }

}
