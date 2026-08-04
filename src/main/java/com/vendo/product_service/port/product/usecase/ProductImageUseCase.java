package com.vendo.product_service.port.product.usecase;

import com.vendo.product_service.domain.image.model.Image;

import java.util.List;

public interface ProductImageUseCase {

    void upload(String productId, List<Image> images);

    void delete(String productId, String imageKey);

}
