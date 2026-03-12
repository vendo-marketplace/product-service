package com.vendo.product_service.application.product;

import com.vendo.product_service.application.category.validation.attribute.AttributesValidator;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.port.product.ProductCommandPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.port.user.CurrentUserPort;
import com.vendo.security_lib.exception.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductService {

    private final ProductCommandPort productCommandPort;
    private final ProductQueryPort productQueryPort;

    private final AttributesValidator attributesValidator;
    private final CategoryQueryPort categoryQueryPort;

    private final CurrentUserPort currentUserPort;

    public Product findById(String id) {
        return productQueryPort.findById(id);
    }

    public void save(Product product) {
        attributesValidator.validateAttributes(product.getCategoryId(), product.getAttributes());
        throwIfCategoryNotFound(product.getCategoryId());

        product.setOwnerId(currentUserPort.getCurrentUserId());
        product.setActive(true);
        productCommandPort.save(product);
    }

    public void update(String id, Product product) {
        Product existing = productQueryPort.findById(id);

        throwIfNotOwnerOfProduct(existing.getOwnerId());
        if (product.getCategoryId() != null) throwIfCategoryNotFound(product.getCategoryId());

        productCommandPort.update(id, product);
    }

    private void throwIfCategoryNotFound(String id) {
        if (!categoryQueryPort.existsById(id)) {
            throw new CategoryNotFoundException("Category not found.");
        }
    }

    private void throwIfNotOwnerOfProduct(String ownerId) {
        if (!ownerId.equals(currentUserPort.getCurrentUserId())) {
            throw new AccessDeniedException("You're not product's owner.");
        }
    }

}