package com.vendo.product_service.application.category;

import com.vendo.core_lib.utils.CollectionUtils;
import com.vendo.core_lib.utils.StringUtils;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.category.CategoryCommandUseCase;
import com.vendo.product_service.port.category.TypeValidationPort;
import com.vendo.product_service.port.id.IdGenerationPort;
import com.vendo.product_service.port.category.CategoryCommandPort;
import com.vendo.product_service.port.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryCommandService implements CategoryCommandUseCase {

    private final TypeValidationPort typeValidationPort;
    private final IdGenerationPort idGenerationPort;

    private final CategoryCommandPort categoryCommandPort;
    private final CategoryQueryPort categoryQueryPort;

    @Override
    @CacheEvict(value = "category-tree", allEntries = true)
    public void save(Category category) {
        typeValidationPort.validate(category);

        category.setId(idGenerationPort.generate());
        category.setPath(category.buildPath(getParentPath(category)));

        if (CollectionUtils.isEmpty(category.getAttributes())) {
            category.setAttributes(new ArrayList<>());
        }

        categoryCommandPort.save(category);
    }

    private List<String> getParentPath(Category category) {
        if (StringUtils.isEmpty(category.getParentId())) {
            return Collections.emptyList();
        }

        Category parent = categoryQueryPort.findById(category.getParentId(), "Parent category not found.");
        return parent.getPath();
    }
}