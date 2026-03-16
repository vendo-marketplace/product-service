package com.vendo.product_service.application.category.validation.attribute;

import com.vendo.product_service.domain.category.model.Category;

import java.util.List;
import java.util.Map;

public interface AttributesValidator {

    void validate(Category category, Map<String, List<String>> attributes);

}
