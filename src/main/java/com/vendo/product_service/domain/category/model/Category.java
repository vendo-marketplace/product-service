package com.vendo.product_service.domain.category.model;

import com.vendo.product_service.adapter.model.category.embedded.AttributeDefinition;
import com.vendo.product_service.domain.category.exception.CategoryValidationException;
import com.vendo.product_service.domain.category.type.CategoryType;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

@Data
@Getter
@Builder
public class Category {

    private String id;
    private String title;
    private String code;
    private String parentId;
    private Map<String, AttributeDefinition> attributes;

    public CategoryType getType() {

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
