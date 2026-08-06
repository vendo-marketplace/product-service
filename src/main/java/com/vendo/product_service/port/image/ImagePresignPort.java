package com.vendo.product_service.port.image;

import com.vendo.product_service.adapter.image.out.aws.dto.nested.PresignType;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.domain.image.model.PresignImage;

import java.util.List;

public interface ImagePresignPort {

    List<PresignImage> generate(PresignType type, List<Image> images);

}
