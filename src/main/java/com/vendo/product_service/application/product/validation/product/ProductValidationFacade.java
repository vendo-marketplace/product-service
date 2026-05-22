package com.vendo.product_service.application.product.validation.product;

import com.vendo.product_service.application.category.validation.attribute.AttributesValidator;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeValue;
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
import com.vendo.utils_lib.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductValidationFacade implements ProductValidationPort {

    private final AttributesValidator attributesValidator;

    private final AttributeQueryPort attributeQueryPort;
    private final CategoryQueryPort categoryQueryPort;
    private final ProductQueryPort productQueryPort;

    private final CurrentUserPort currentUserPort;

    @Override
    public Category validateCategoryOnSave(String categoryId) {
        Category category = categoryQueryPort.findById(categoryId, "Parent category not found.");
        category.throwIfNotDesiredType(CategoryType.CHILD, "Category type should be child.");
        return category;
    }

    @Override
    public List<Attribute> validateAttributesOnSave(List<String> originAttributeIds, List<AttributeValue> requestAttributes) {
        List<Attribute> originAttributes = attributeQueryPort.findAllByIds(originAttributeIds);
        attributesValidator.validate(originAttributes, requestAttributes);
        return originAttributes;
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
        if (StringUtils.isEmpty(id)) return;

        if (!categoryQueryPort.existsById(id)) {
            throw new CategoryNotFoundException("Category not found.");
        }
    }
}
