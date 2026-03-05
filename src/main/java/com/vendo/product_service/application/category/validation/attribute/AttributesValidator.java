package com.vendo.product_service.application.category.validation.attribute;

import java.util.List;
import java.util.Map;

public interface AttributesValidator {

    void validateAttributes(String categoryId, Map<String, List<String>> attributes);

}
