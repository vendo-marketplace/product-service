package com.vendo.product_service.application.product.validation.product;

import com.vendo.product_service.application.category.validation.attribute.AttributesValidator;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryType;
import com.vendo.product_service.domain.product.exception.NotProductOwnerException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.out.attribute.AttributeQueryPort;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import com.vendo.product_service.port.out.product.ProductQueryPort;
import com.vendo.product_service.port.out.product.ProductValidationPort;
import com.vendo.product_service.port.out.user.CurrentUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductValidationFacade implements ProductValidationPort {

    private final AttributeQueryPort attributeQueryPort;
    private final AttributesValidator attributesValidator;
    private final CategoryQueryPort categoryQueryPort;
    private final ProductQueryPort productQueryPort;
    private final CurrentUserPort currentUserPort;

    @Override
    public void validateOnSave(Product product) {
        Category category = categoryQueryPort.findById(product.getCategoryId(), "Parent category not found.");
        category.throwIfNotDesiredType(CategoryType.CHILD, "Category type should be child.");
        List<Attribute> originAttributes = attributeQueryPort.findAllByIdsIn(category.getAttributes());
        attributesValidator.validate(originAttributes, product.getAttributes());
    }

    @Override
    public void validateOnUpdate(String id, Product product) {
        Product existing = productQueryPort.findById(id);
        throwIfNotOwnerOfProduct(existing.getOwnerId());
        throwIfCategoryNotExists(product.getCategoryId());
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
}
