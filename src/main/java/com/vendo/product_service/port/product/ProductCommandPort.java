package com.vendo.product_service.port.product;


import com.vendo.product_service.domain.product.model.Product;

public interface ProductCommandPort {

    void save(Product product);

    void update(String id, Product product);

}