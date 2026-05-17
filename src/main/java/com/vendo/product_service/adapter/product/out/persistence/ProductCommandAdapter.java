package com.vendo.product_service.adapter.product.out.persistence;

import com.vendo.product_service.adapter.product.out.mapper.MongoProductMapper;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.out.product.ProductCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCommandAdapter implements ProductCommandPort {

    private final ProductRepository repository;
    private final MongoProductMapper mapper;

    @Override
    public String save(Product product) {
        MongoProduct entity = mapper.toEntity(product);
        MongoProduct created = repository.save(entity);
        return created.getId();
    }

    @Override
    public void update(String id, Product product) {
        MongoProduct entity = findOrThrow(id);
        mapper.updateEntity(entity, product);
        repository.save(entity);
    }

    private MongoProduct findOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found."));
    }
}