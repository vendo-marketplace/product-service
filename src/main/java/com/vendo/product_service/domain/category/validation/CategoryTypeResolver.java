package com.vendo.product_service.domain.category.validation;

import com.vendo.product_service.domain.category.model.CategoryType;
import com.vendo.product_service.adapter.model.category.embedded.AttributeDefinition;

import java.util.Map;

public interface CategoryTypeResolver {

    CategoryType resolve(String parentId, Map<String, AttributeDefinition> attributes);

}
