package com.vendo.product_service.port.image.usecase;

import com.vendo.product_service.domain.image.model.PresignType;
import com.vendo.product_service.domain.image.model.Image;

import java.util.List;

public interface ImageUseCase {

    List<String> upload(PresignType type, List<Image> images);

    String upload(PresignType type, Image image);

}
