package com.vendo.product_service.application;


import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.port.CategoryQueryPort;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.domain.product.port.ProductCommandPort;
import com.vendo.product_service.domain.product.port.ProductQueryPort;
import com.vendo.security.common.exception.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.vendo.product_service.security.common.helper.SecurityContextHelper.getUserIdFromContext;

@Component
@RequiredArgsConstructor
public class ProductUseCase {

    private final ProductCommandPort commandPort;
    private final ProductQueryPort queryPort;
    private final CategoryQueryPort categoryQueryPort;

    public void save(Product product) {
        if (product.getCategoryId() != null && !categoryQueryPort.existsById(product.getCategoryId())) {
            throw new CategoryNotFoundException("Category not found.");
        }

        commandPort.save(product);
    }

    public void update(String id, Product updatedProduct) {
        Product existing = queryPort.findById(id);

        if (!existing.getOwnerId().equals(getUserIdFromContext())) {
            throw new AccessDeniedException("Only owner can edit product.");
        }

        Optional.ofNullable(updatedProduct.getTitle()).ifPresent(existing::setTitle);
        Optional.ofNullable(updatedProduct.getDescription()).ifPresent(existing::setDescription);
        Optional.ofNullable(updatedProduct.getQuantity()).ifPresent(existing::setQuantity);
        Optional.ofNullable(updatedProduct.getPrice()).ifPresent(existing::setPrice);
        Optional.ofNullable(updatedProduct.getAttributes()).ifPresent(existing::setAttributes);
        Optional.ofNullable(updatedProduct.getActive()).ifPresent(existing::setActive);

        if (updatedProduct.getCategoryId() != null &&
                !categoryQueryPort.existsById(updatedProduct.getCategoryId())) {
            throw new CategoryNotFoundException("Category not found.");
        } else {
            existing.setCategoryId(updatedProduct.getCategoryId());
        }

        commandPort.save(existing);
    }

    public Product findById(String id) {
        return queryPort.findById(id);
    }
}