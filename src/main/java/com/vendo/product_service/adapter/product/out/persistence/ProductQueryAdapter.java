package com.vendo.product_service.adapter.product.out.persistence;

import com.vendo.product_service.adapter.product.out.mapper.MongoProductMapper;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductQueryAdapter implements ProductQueryPort {

    private final ProductRepository repository;
    private final MongoProductMapper mapper;

    @Override
    public Product findById(String id) {
        MongoProduct entity = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found."));
        return mapper.toProduct(entity);
    }

    @Override
    public boolean existsById(String productId) {
        return repository.existsById(productId);
    }

    @Override
    public List<Product> findAllByIds(List<String> productIds) {
        List<MongoProduct> allById = repository.findAllById(productIds);
        return mapper.toProducts(allById);
    }

    @Override
    public List<Product> requireAllByIds(List<String> productIds) {
        Map<String, Product> productsById = findAllByIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        return productIds.stream()
                .map(id -> {
                    Product product = productsById.get(id);
                    if (product == null) {
                        throw new ProductNotFoundException("Product not found by id: %s.".formatted(id));
                    }
                    return product;
                })
                .toList();
    }
}