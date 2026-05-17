package com.vendo.product_service.application.category;

import com.vendo.product_service.domain.category.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.in.category.CategoryCommandUseCase;
import com.vendo.product_service.port.out.IdGenerationPort;
import com.vendo.product_service.port.out.category.CategoryCommandPort;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryCommandService implements CategoryCommandUseCase {

    private final CategoryCommandPort categoryCommandPort;
    private final CategoryQueryPort categoryQueryPort;
    private final IdGenerationPort idGenerationPort;

    @Override
    @CacheEvict(value = "category-tree", allEntries = true)
    public void save(Category category) {
        throwIfExistsByCode(category.getCode());

        category.setId(idGenerationPort.generate());
        category.setPath(category.buildPath(getParentPath(category)));
        category.setAttributes(new ArrayList<>());

        categoryCommandPort.save(category);
    }

    private void throwIfExistsByCode(String code) {
        if (categoryQueryPort.existsByCode(code)) {
            throw new CategoryAlreadyExistsException("Category already exists by code." );
        }
    }

    private List<String> getParentPath(Category category) {
        if (category.getParentId() == null) return Collections.emptyList();
        Category parent = categoryQueryPort.findById(category.getParentId(), "Parent category not found." );
        return parent.getPath();
    }
}