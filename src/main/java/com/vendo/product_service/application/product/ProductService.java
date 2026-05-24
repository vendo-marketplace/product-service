package com.vendo.product_service.application.product;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.in.product.ProductUseCase;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import com.vendo.product_service.port.out.product.ProductCommandPort;
import com.vendo.product_service.port.out.product.ProductEventSenderPort;
import com.vendo.product_service.port.out.product.ProductQueryPort;
import com.vendo.product_service.port.out.product.ProductValidationPort;
import com.vendo.product_service.port.out.user.CurrentUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductService implements ProductUseCase {

    private final CurrentUserPort currentUserPort;

    private final CategoryQueryPort categoryQueryPort;

    private final ProductValidationPort validationPort;
    private final ProductCommandPort productCommandPort;
    private final ProductQueryPort productQueryPort;
    private final ProductEventSenderPort eventSenderPort;

    @Override
    public Product findById(String id) {
        return productQueryPort.findById(id);
    }

    @Override
    @Transactional
    public void save(Product product) {
        Category category = validationPort.validateCategoryOnSave(product.getCategoryId());
        List<Attribute> attributes = validationPort.validateAttributes(category.getAttributes(), product.getAttributes());

        product.setOwnerId(currentUserPort.getCurrentUserId());
        product.setActive(true);

        Product saved = productCommandPort.save(product);
        eventSenderPort.sendCreated(saved, attributes);
    }

    @Override
    @Transactional
    public void update(String id, Product product) {
        product.setId(id);

        Product existing = productQueryPort.findById(id);
        validationPort.validateProductOwnerOnUpdate(existing);
        Category category = categoryQueryPort.findById(product.getCategoryId());
        List<Attribute> attributes = validationPort.validateAttributes(category.getAttributes(), product.getAttributes());

        productCommandPort.update(id, product);
        eventSenderPort.sendUpdated(product, attributes);
    }
}