package com.vendo.product_service.infrastructure.shared.annotation;

import com.vendo.product_service.infrastructure.shared.utils.ImageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ImageValidator implements ConstraintValidator<ImageFile, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        return ImageUtils.isValid(file);
    }

}
