package com.vendo.product_service.domain.category.validation;

import com.vendo.product_service.domain.category.common.exception.CategoryValidationException;
import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DefaultCategoryTypeResolver implements CategoryTypeResolver {

    @Override
    public CategoryType resolve(String parentId, Map<String, AttributeDefinition> attributes) {
        if (isParent(parentId, attributes)) {
            return CategoryType.PARENT;
        }

        if (isSub(parentId, attributes)) {
            return CategoryType.SUB;
        }

        if (isChild(parentId, attributes)) {
            return CategoryType.CHILD;
        }

        throw new CategoryValidationException("Invalid category structure.");
    }

    private boolean isParent(String parentId, Map<String, AttributeDefinition> attributes) {
        return parentId == null && attributes == null;
    }

    private boolean isSub(String parentId, Map<String, AttributeDefinition> attributes) {
        return StringUtils.isNotEmpty(parentId) && attributes == null;
    }

    private boolean isChild(String parentId, Map<String, AttributeDefinition> attributes) {
        return StringUtils.isNotEmpty(parentId)
                && attributes != null
                && !attributes.isEmpty();
    }
}
