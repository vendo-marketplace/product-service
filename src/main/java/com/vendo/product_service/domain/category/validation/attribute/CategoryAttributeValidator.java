package com.vendo.product_service.domain.category.validation.attribute;

import java.util.List;
import java.util.Map;

public interface CategoryAttributeValidator {

    void validateCategoryAttributes(String categoryId, Map<String, List<String>> requestAttributes);

}
