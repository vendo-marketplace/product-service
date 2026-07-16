package com.vendo.product_service.adapter.image.in.controller.factory;

import com.vendo.product_service.domain.image.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageFactory {

    List<Image> create(List<MultipartFile> images);

}
