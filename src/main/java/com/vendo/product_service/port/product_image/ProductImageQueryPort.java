package com.vendo.product_service.port.product_image;

import com.vendo.product_service.domain.product_image.model.ProductImage;

import java.util.List;

public interface ProductImageQueryPort {

    List<ProductImage> findAllBy(List<String> keys);

}
