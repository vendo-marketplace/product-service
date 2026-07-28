package com.vendo.product_service.application.product;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.domain.user.User;
import com.vendo.product_service.port.product.usecase.ProductUseCase;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.port.product.ProductCommandPort;
import com.vendo.product_service.port.product.ProductEventSenderPort;
import com.vendo.product_service.port.product.ProductQueryPort;
import com.vendo.product_service.port.user.AuthUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class ProductService implements ProductUseCase {

    private final AuthUserPort authUserPort;

    private final CategoryQueryPort categoryQueryPort;

    private final ProductValidationFacade productValidationFacade;
    private final ProductCommandPort productCommandPort;
    private final ProductQueryPort productQueryPort;
    private final ProductEventSenderPort eventSenderPort;

    @Override
    public Product findById(String id) {
        return productQueryPort.findById(id);
    }

    @Override
    public void save(Product request) {
        Category category = productValidationFacade.validateCategoryOnSave(request.getCategoryId());
        List<Attribute> attributes = productValidationFacade.validateAttributes(category.getAttributes(), request.getAttributes());

        request.setOwnerId(authUserPort.getAuthUser().id());
        request.setActive(true);

        Product saved = productCommandPort.save(request);
        eventSenderPort.sendCreated(saved, attributes);
    }

    @Override
    public void update(String id, Product request) {
        request.setId(id);

        Product existing = productQueryPort.findById(id);
        User authUser = authUserPort.getAuthUser();
        authUser.throwIfNotOwner(existing.getOwnerId());

        Category category = categoryQueryPort.findById(existing.getCategoryId());
        List<Attribute> attributes = productValidationFacade.validateAttributes(category.getAttributes(), request.getAttributes());

        productCommandPort.update(id, request);
        eventSenderPort.sendUpdated(request, attributes);
    }
}