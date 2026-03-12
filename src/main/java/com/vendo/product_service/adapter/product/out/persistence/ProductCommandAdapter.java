package com.vendo.product_service.adapter.product.out.persistence;

import com.vendo.product_service.adapter.product.out.mapper.MongoProductMapper;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.product.ProductCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCommandAdapter implements ProductCommandPort {

    private final ProductRepository productRepository;
    private final MongoProductMapper mongoProductMapper;

    @Override
    public void save(Product product) {
        MongoProduct entity = mongoProductMapper.toEntity(product);
        productRepository.save(entity);
    }

    @Override
    public void update(String id, Product product) {
        MongoProduct entity = findOrThrow(id);
        mongoProductMapper.updateEntity(product, entity);
        productRepository.save(entity);
    }

    private MongoProduct findOrThrow(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found."));
    }
}