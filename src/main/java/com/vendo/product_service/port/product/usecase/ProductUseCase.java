package com.vendo.product_service.port.product.usecase;

import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.domain.product.model.Product;

import java.util.List;

public interface ProductUseCase {

    void save(List<Image> images, Product product);

    void update(String id, Product product);

    Product findById(String id);

}
