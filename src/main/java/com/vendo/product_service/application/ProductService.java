package com.vendo.product_service.application;

import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.port.product.ProductCommandPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductService {

    private final ProductCommandPort productCommandPort;
    private final ProductQueryPort productQueryPort;
    private final CategoryQueryPort categoryQueryPort;

    public Product findById(String id) {
        return productQueryPort.findById(id);
    }

    public void save(Product product) {
        throwIfCategoryNotFound(product.getCategoryId());
        productCommandPort.save(product);
    }

    public void update(String id, Product product) {
        productCommandPort.update(id, product);
    }

    private void throwIfCategoryNotFound(String id) {
        if (id != null && !categoryQueryPort.existsById(id)) {
            throw new CategoryNotFoundException("Category not found.");
        }
    }
}