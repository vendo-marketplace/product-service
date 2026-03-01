package com.vendo.product_service.adapter.out.product;


import com.vendo.product_service.adapter.out.product.mapper.ProductMapper;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.adapter.model.product.MongoProduct;
import com.vendo.product_service.adapter.out.product.repository.ProductRepository;
import com.vendo.product_service.port.product.ProductQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductQueryAdapter implements ProductQueryPort {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public Product findById(String id) {
        MongoProduct entity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found."));
        return productMapper.toProduct(entity);
    }

    @Override
    public boolean existsById(String productId) {
        return productRepository.existsById(productId);
    }
}