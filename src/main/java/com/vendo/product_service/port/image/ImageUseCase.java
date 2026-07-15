package com.vendo.product_service.port.image;

import com.vendo.product_service.domain.image.model.Image;

import java.util.List;

public interface ImageUseCase {

    void upload(String productId, List<Image> images);


}
