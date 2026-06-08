package com.vendo.product_service.port.out.product;

import com.vendo.product_service.domain.product.model.Product;

import java.util.List;

public interface ProductQueryPort {

    Product findById(String id);
    boolean existsById(String id);
    List<Product> findAllByIds(List<String> productIds);

}