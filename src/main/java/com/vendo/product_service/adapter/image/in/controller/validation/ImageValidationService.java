package com.vendo.product_service.adapter.image.in.controller.validation;

import com.vendo.product_service.domain.image.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageValidationService {

    List<Image> validate(List<MultipartFile> images);

}
