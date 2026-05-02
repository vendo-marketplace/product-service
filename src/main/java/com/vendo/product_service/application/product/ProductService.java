package com.vendo.product_service.application.product;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeValue;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.product.exception.NotProductOwnerException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.in.product.ProductUseCase;
import com.vendo.product_service.port.out.attribute.AttributeQueryPort;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import com.vendo.product_service.port.out.product.ProductCommandPort;
import com.vendo.product_service.port.out.product.ProductEventSenderPort;
import com.vendo.product_service.port.out.product.ProductQueryPort;
import com.vendo.product_service.port.out.user.CurrentUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductService implements ProductUseCase {

    private final CurrentUserPort currentUserPort;

    private final ProductCommandPort productCommandPort;
    private final ProductQueryPort productQueryPort;
    private final ProductEventSenderPort eventSenderPort;

    private final AttributeQueryPort attributeQueryPort;
    private final CategoryQueryPort categoryQueryPort;

    @Override
    public Product findById(String id) {
        return productQueryPort.findById(id);
    }

    @Override
    @Transactional
    public void save(Product product) {
        product.setOwnerId(currentUserPort.getCurrentUserId());
        product.setActive(true);
        productCommandPort.save(product);
        eventSenderPort.sendCreated(product);
    }

    @Override
    @Transactional
    public void update(String id, Product product) {
        Product existing = productQueryPort.findById(id);

        throwIfNotOwnerOfProduct(existing.getOwnerId());
        throwIfCategoryNotExists(product.getCategoryId());

        List<Attribute> attributes = loadAttributesFor(product);
        productCommandPort.update(id, product);
        eventSenderPort.sendUpdated(product, attributes);
    }

    private void throwIfNotOwnerOfProduct(String ownerId) {
        if (!ownerId.equals(currentUserPort.getCurrentUserId())) {
            throw new NotProductOwnerException("You're not product's owner.");
        }
    }

    private void throwIfCategoryNotExists(final String id) {
        if (id == null || id.isBlank()) return;

        if (!categoryQueryPort.existsById(id)) {
            throw new CategoryNotFoundException("Category not found.");
        }
    }

    private List<Attribute> loadAttributesFor(Product product) {
        List<String> attributeIds = product.getAttributes().stream().map(AttributeValue::id).toList();
        return attributeQueryPort.findAllByIdsIn(attributeIds);
    }
}