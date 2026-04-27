package com.vendo.product_service.application.product;

import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.product.exception.NotProductOwnerException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.in.product.ProductUseCase;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import com.vendo.product_service.port.out.product.ProductCommandPort;
import com.vendo.product_service.port.out.product.ProductQueryPort;
import com.vendo.product_service.port.out.user.CurrentUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductService implements ProductUseCase {
    private final ProductCommandPort productCommandPort;

    private final ProductQueryPort productQueryPort;
    private final CategoryQueryPort categoryQueryPort;
    private final CurrentUserPort currentUserPort;

    @Override
    public Product findById(String id) {
        return productQueryPort.findById(id);
    }

    @Override
    public void save(Product product) {
        product.setOwnerId(currentUserPort.getCurrentUserId());
        product.setActive(true);
        productCommandPort.save(product);
    }

    @Override
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