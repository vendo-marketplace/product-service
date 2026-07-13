package com.vendo.product_service.port.product_image;

import com.vendo.product_service.domain.product_image.model.ProductImage;
import com.vendo.product_service.domain.product_image.model.ProductImageStatus;

import java.util.List;

public interface ProductImageCommandPort {

    void save(ProductImage productImage);

    void updateAllBy(List<String> keys, ProductImageStatus status);

}
