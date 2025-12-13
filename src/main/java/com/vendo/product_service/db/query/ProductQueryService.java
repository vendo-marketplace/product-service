package com.vendo.product_service.db.query;

import com.vendo.product_service.common.exception.ProductNotFoundException;
import com.vendo.product_service.db.model.Product;
import com.vendo.product_service.db.repository.ProductRepository;
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
