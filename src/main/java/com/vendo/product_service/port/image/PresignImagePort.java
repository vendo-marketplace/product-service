package com.vendo.product_service.port.image;

import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.domain.image.model.PresignedImage;

import java.util.List;

public interface PresignImagePort {

    List<PresignedImage> generate(List<Image> images);

}
