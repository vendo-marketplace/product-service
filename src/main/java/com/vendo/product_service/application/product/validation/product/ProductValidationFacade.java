package com.vendo.product_service.application.product.validation.product;

import com.vendo.product_service.application.category.validation.attribute.AttributeValidator;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeValue;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryType;
import com.vendo.product_service.domain.product.exception.NotProductOwnerException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.out.attribute.AttributeQueryPort;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import com.vendo.product_service.port.out.product.ProductValidationPort;
import com.vendo.product_service.port.out.user.AuthUserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductValidationFacade implements ProductValidationPort {

    private final AttributeValidator attributeValidator;

    private final AttributeQueryPort attributeQueryPort;
    private final CategoryQueryPort categoryQueryPort;
    private final AuthUserPort authUserPort;

    @Override
    public List<Attribute> validateAttributes(List<String> originAttributeIds, List<AttributeValue> requestAttributes) {
        List<Attribute> originAttributes = attributeQueryPort.findAllByIds(originAttributeIds);
        attributeValidator.validate(originAttributes, requestAttributes);
        return originAttributes;
    }

    @Override
    public Category validateCategoryOnSave(String categoryId) {
        Category category = categoryQueryPort.findById(categoryId);
        category.throwIfNotDesiredType(CategoryType.CHILD, "Category type should be child.");
        return category;
    }

    @Override
    public void validateProductOwnerOnUpdate(Product product) {
        String ownerId = product.getOwnerId();
        if (!ownerId.equals(authUserPort.getAuthUser().id())) {
            throw new NotProductOwnerException("You're not product's owner.");
        }
    }
}
