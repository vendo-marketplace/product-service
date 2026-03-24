package com.vendo.product_service.application.product;

import com.vendo.product_service.application.category.validation.attribute.AttributesValidator;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.product.exception.NotProductOwnerException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.port.product.ProductCommandPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.port.user.CurrentUserPort;
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
        Category category = categoryQueryPort.findById(product.getCategoryId(), "Parent category not found.");
        attributesValidator.validate(category, product.getAttributes());

        product.setOwnerId(currentUserPort.getCurrentUserId());
        product.setActive(true);

        productCommandPort.save(product);
    }

    public void update(String id, Product product) {
        Product existing = productQueryPort.findById(id);

        throwIfNotOwnerOfProduct(existing.getOwnerId());
        if (product.getCategoryId() != null) throwIfCategoryNotExists(product.getCategoryId());

        productCommandPort.update(id, product);
    }

    private void throwIfNotOwnerOfProduct(String ownerId) {
        if (!ownerId.equals(currentUserPort.getCurrentUserId())) {
            throw new NotProductOwnerException("You're not product's owner.");
        }
    }

    private void throwIfCategoryNotExists(String id) {
        if (!categoryQueryPort.existsById(id)) {
            throw new CategoryNotFoundException("Category not found.");
        }
    }

}