package com.vendo.product_service.adapter.product.out.persistence;

import com.vendo.product_service.adapter.product.out.mapper.MongoProductMapper;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductQueryAdapter implements ProductQueryPort {

    private final ProductRepository productRepository;
    private final MongoProductMapper mongoProductMapper;

    @Override
    public Product findById(String id) {
        MongoProduct entity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found."));
        return mongoProductMapper.toProduct(entity);
    }

    @Override
    public boolean existsById(String productId) {
        return productRepository.existsById(productId);
    }
}