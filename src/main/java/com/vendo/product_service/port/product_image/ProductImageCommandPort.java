package com.vendo.product_service.port.product_image;

import com.vendo.product_service.domain.product_image.model.ProductImage;

public interface ProductImageCommandPort {

    void save(ProductImage productImage);

}
