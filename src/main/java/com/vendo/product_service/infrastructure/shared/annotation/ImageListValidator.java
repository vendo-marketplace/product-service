package com.vendo.product_service.infrastructure.shared.annotation;

import com.vendo.product_service.infrastructure.shared.utils.ImageUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ImageListValidator implements ConstraintValidator<ImageFile, List<MultipartFile>> {

    @Override
    public boolean isValid(List<MultipartFile> value, ConstraintValidatorContext context) {
        return ImageUtils.isValid(value);
    }

}
