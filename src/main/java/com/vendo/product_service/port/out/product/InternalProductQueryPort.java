package com.vendo.product_service.port.out.product;

import com.vendo.product_service.domain.product.model.Product;

import java.time.Instant;
import java.util.List;

public interface InternalProductQueryPort {

    List<Product> getAll(Instant cursor, int limit);

}
