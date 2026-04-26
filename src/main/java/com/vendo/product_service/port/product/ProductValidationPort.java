package com.vendo.product_service.port.product;

import com.vendo.product_service.adapter.product.in.dto.CreateProductRequest;

public interface ProductValidationPort {

    void validateOnSave(CreateProductRequest request);

}
