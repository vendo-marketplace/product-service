package com.vendo.product_service.domain.product.db.cqrs.query;

import com.vendo.product_service.domain.product.common.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.db.model.Product;
import com.vendo.product_service.domain.product.db.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductQueryService {

    private final ProductRepository productRepository;

    public Product findById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found."));
    }

}
