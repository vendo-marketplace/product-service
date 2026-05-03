package com.vendo.product_service.port.out.product;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.product.model.Product;

import java.util.List;

public interface ProductValidationPort {

    void validateOnSave(Product product, List<Attribute> attributes);
    void validateOnUpdate(String id, Product product);

}
