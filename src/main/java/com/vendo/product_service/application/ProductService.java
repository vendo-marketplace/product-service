package com.vendo.product_service.application;

import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.port.category.CategoryQueryPort;
import com.vendo.product_service.domain.port.product.ProductCommandPort;
import com.vendo.product_service.domain.port.product.ProductQueryPort;
import com.vendo.product_service.domain.port.security.CurrentUserPort;
import com.vendo.product_service.domain.product.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductService {

    private final ProductCommandPort productCommandPort;
    private final ProductQueryPort productQueryPort;
    private final CategoryQueryPort categoryQueryPort;
    private final CurrentUserPort currentUserPort;

    public Product findById(String id) {
        return productQueryPort.findById(id);
    }

    public void save(Product product) {
        product.setOwnerId(currentUserPort.getCurrentUserId());
        product.setActive(true);
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