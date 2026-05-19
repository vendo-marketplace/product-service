package com.vendo.product_service.port.out.product;

import com.vendo.product_service.domain.product.model.Product;

public interface ProductCommandPort {

    Product save(Product product);

    void update(String id, Product product);

}