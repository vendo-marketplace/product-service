package com.vendo.product_service.port.image;

import com.vendo.product_service.domain.image.model.Image;

public interface ImageCommandPort {

    void save(Image image);

}
