package com.vendo.product_service.domain.category.validation;

import com.vendo.product_service.domain.category.db.model.embedded.AttributeDefinition;
import com.vendo.product_service.domain.category.web.dto.CreateCategoryRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CreateCategoryValidationService {

    public void validateCategoryOnSave(CreateCategoryRequest createCategoryRequest) {
        if (isParent(createCategoryRequest.parentId(), createCategoryRequest.attributes())) {
            // parent category
        } else if (isSub(createCategoryRequest.parentId(), createCategoryRequest.attributes())) {
            // sub category
        } else if (isChild(createCategoryRequest.parentId(), createCategoryRequest.attributes())) {
            // child category
        } else {
            // invalid category
        }
    }

    private boolean isParent(String parentId, Map<String, AttributeDefinition> attributes) {
        return parentId == null && attributes == null;
    }

    private boolean isSub(String parentId, Map<String, AttributeDefinition> attributes) {
        return !StringUtils.isEmpty(parentId) && attributes == null;
    }

    private boolean isChild(String parentId, Map<String, AttributeDefinition> attributes) {
        return !StringUtils.isEmpty(parentId)
                && (attributes != null && !attributes.isEmpty());
    }

}
