package com.vendo.product_service.adapter.out.product;

import com.vendo.product_service.adapter.out.product.mapper.ProductEntityMapper;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.adapter.model.product.ProductEntity;
import com.vendo.product_service.adapter.out.product.repository.ProductRepository;
import com.vendo.product_service.domain.product.port.ProductCommandPort;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCommandAdapter implements ProductCommandPort {

    private final ProductRepository productRepository;
    private final ProductEntityMapper productEntityMapper;

    @Override
    public void save(Product product) {
        ProductEntity entity = productEntityMapper.toProductEntityFromProductDomain(product);
        productRepository.save(entity);
    }
}