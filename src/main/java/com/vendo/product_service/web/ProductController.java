package com.vendo.product_service.web;

import com.vendo.product_service.common.mapper.ProductMapper;
import com.vendo.product_service.model.Product;
import com.vendo.product_service.service.ProductService;
import com.vendo.product_service.web.dto.CreateProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController("/products")
public class ProductController {

    private final ProductMapper productMapper;

    private final ProductService productService;

    @PostMapping
    public void save(@RequestBody CreateProductRequest createProductRequest) {
        Product product = productMapper.toProduct(createProductRequest);

        product.setActive(true);

        productService.save(product);
    }

    @PutMapping
    public void update() {

    }

    @GetMapping
    public void find() {

    }

}
