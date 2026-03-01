package com.vendo.product_service.application.category.validation.attribute;

import java.util.List;
import java.util.Map;

public interface CategoryAttributeValidator {

    void validateCategoryAttributes(String requestCategoryId, Map<String, List<String>> requestAttributes);

}
