package com.vendo.product_service.port.product;


import com.vendo.product_service.domain.product.model.Product;

public interface ProductQueryPort {
    Product findById(String id);
    boolean existsById(String productId);
}