package com.vendo.product_service.port.image;

import com.vendo.product_service.domain.image.model.Image;

import java.util.Map;

public interface ImageUploadPort {

    void upload(Map<String, Image> images);

}
