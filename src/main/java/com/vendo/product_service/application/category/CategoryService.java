package com.vendo.product_service.application.category;

import com.vendo.product_service.domain.category.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.in.category.CategoryUseCase;
import com.vendo.product_service.port.out.IdGenerationPort;
import com.vendo.product_service.port.out.category.CategoryCommandPort;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryService implements CategoryUseCase {

    private final CategoryCommandPort categoryCommandPort;
    private final CategoryQueryPort categoryQueryPort;
    private final IdGenerationPort idGenerationPort;

    @Override
    public Category findById(String id) {
        return categoryQueryPort.findById(id, "Category not found.");
    }

    @Override
    public void save(Category category) {
        throwIfExistsByCode(category.getCode());
        category.setId(idGenerationPort.generate());
        category.setPath(category.buildPath(getParentPath(category)));
        categoryCommandPort.save(category);
    }

    private void throwIfExistsByCode(String code) {
        if (categoryQueryPort.existsByCode(code)) {
            throw new CategoryAlreadyExistsException("Category already exists by code.");
        }
    }

    private List<String> getParentPath(Category category) {
        if (category.getParentId() == null) return Collections.emptyList();
        Category parent = categoryQueryPort.findById(category.getParentId(), "Parent category not found.");
        return parent.getPath();
    }
}