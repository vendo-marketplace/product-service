package com.vendo.product_service.domain.category.validation;

import java.util.List;
import java.util.Map;

public interface CategoryValidationFacade {

    void validateCategoryOnSave(String categoryId, Map<String, List<String>> attributes);

}
