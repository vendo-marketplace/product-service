package com.vendo.product_service.infrastructure.shared.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
@Constraint(validatedBy = {ImageValidator.class, ImageListValidator.class})
public @interface ImageFile {

    String message() default "File is not image or empty.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
