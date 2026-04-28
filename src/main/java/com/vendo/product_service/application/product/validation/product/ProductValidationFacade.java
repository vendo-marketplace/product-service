package com.vendo.product_service.application.product.validation.product;

import com.vendo.product_service.adapter.product.in.dto.CreateProductRequest;
import com.vendo.product_service.application.category.validation.attribute.AttributesValidator;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryType;
import com.vendo.product_service.port.out.attribute.AttributeQueryPort;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import com.vendo.product_service.port.out.product.ProductValidationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductValidationFacade implements ProductValidationPort {

    private final AttributeQueryPort attributeQueryPort;
    private final AttributesValidator attributesValidator;
    private final CategoryQueryPort categoryQueryPort;

    public void validateOnSave(CreateProductRequest request) {
        Category category = categoryQueryPort.findById(request.categoryId(), "Parent category not found.");
        category.throwIfNotDesiredType(CategoryType.CHILD, "Category type should be child.");

        List<Attribute> originAttributes = attributeQueryPort.findAllByIdsIn(category.getAttributes());
        attributesValidator.validate(originAttributes, request.attributes());
    }
}
