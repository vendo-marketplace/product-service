package com.vendo.product_service.port.out.product;

import com.vendo.product_service.domain.product.model.Product;

public interface ProductValidationPort {

    void validateOnSave(Product product);
    void validateOnUpdate(String id, Product product);

}
