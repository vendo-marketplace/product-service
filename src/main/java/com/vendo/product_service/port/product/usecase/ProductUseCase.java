package com.vendo.product_service.port.product.usecase;

import com.vendo.product_service.domain.product.model.Product;

public interface ProductUseCase {

    void save(Product product);

    void update(String id, Product product);

    Product findById(String id);

}
