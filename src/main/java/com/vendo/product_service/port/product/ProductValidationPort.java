package com.vendo.product_service.port.product;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeValue;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.product.model.Product;

import java.util.List;

public interface ProductValidationPort {

    List<Attribute> validateAttributes(List<String> originAttributeIds, List<AttributeValue> requestAttributes);

    Category validateCategoryOnSave(String categoryId);

    void validateProductOwnerOnUpdate(Product product);

}
