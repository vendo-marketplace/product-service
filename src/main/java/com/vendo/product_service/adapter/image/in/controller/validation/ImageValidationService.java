package com.vendo.product_service.adapter.image.in.controller.validation;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageValidationService {

    void validate(List<MultipartFile> images);

}
