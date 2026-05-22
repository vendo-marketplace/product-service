package com.vendo.product_service.port.out.product;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeValue;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.product.model.Product;

import java.util.List;

public interface ProductValidationPort {

    Category validateCategoryOnSave(String categoryId);
    List<Attribute> validateAttributesOnSave(List<String> originAttributeIds, List<AttributeValue> requestAttributes);

    void validateOnUpdate(String id, Product product);

}
