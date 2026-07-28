package com.vendo.product_service.port.product.internal;

import com.vendo.product_service.domain.product.model.Product;

import java.util.List;

public interface InternalProductQueryPort {

    List<Product> getAll(String cursor, int limit);

}
