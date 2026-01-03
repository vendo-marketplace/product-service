package com.vendo.product_service.domain.category.validation;

import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;

import java.util.Map;

public interface CategoryTypeResolver {

    CategoryType resolve(String parentId, Map<String, AttributeDefinition> attributes);

}
