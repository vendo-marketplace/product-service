package com.vendo.product_service.application.product;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeValue;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.in.product.ProductUseCase;
import com.vendo.product_service.port.out.attribute.AttributeQueryPort;
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

    private final ProductValidationPort validationPort;
    private final ProductCommandPort productCommandPort;
    private final ProductQueryPort productQueryPort;
    private final ProductEventSenderPort eventSenderPort;

    private final AttributeQueryPort attributeQueryPort;

    @Override
    public Product findById(String id) {
        return productQueryPort.findById(id);
    }

    @Override
    @Transactional
    public void save(Product product) {
        validationPort.validateOnSave(product);

        product.setOwnerId(currentUserPort.getCurrentUserId());
        product.setActive(true);

        productCommandPort.save(product);
        eventSenderPort.sendCreated(product);
    }

    @Override
    @Transactional
    public void update(String id, Product product) {
        validationPort.validateOnUpdate(id, product);
        List<Attribute> attributes = loadAttributesFor(product);
        productCommandPort.update(id, product);
        eventSenderPort.sendUpdated(product, attributes);
    }

    private List<Attribute> loadAttributesFor(Product product) {
        List<String> attributeIds = product.getAttributes().stream().map(AttributeValue::id).toList();
        return attributeQueryPort.findAllByIdsIn(attributeIds);
    }
}