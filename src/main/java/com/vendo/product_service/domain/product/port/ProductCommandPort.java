package com.vendo.product_service.domain.product.port;


import com.vendo.product_service.domain.product.model.Product;

public interface ProductCommandPort {
    void save(Product product);
}