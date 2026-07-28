package com.vendo.product_service.port.favorite.usecase;

import com.vendo.product_service.domain.product.model.Product;

import java.util.List;

public interface FavoriteUseCase {

    void add(String productId);

    void remove(String productId);

    List<Product> getAll();

}
