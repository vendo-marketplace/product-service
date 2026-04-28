package com.vendo.product_service.application.category;

import com.vendo.product_service.domain.category.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.in.category.CategoryUseCase;
import com.vendo.product_service.port.out.category.CategoryCommandPort;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryService implements CategoryUseCase {

    private final CategoryCommandPort categoryCommandPort;
    private final CategoryQueryPort categoryQueryPort;

    @Override
    public Category findById(String id) {
        return categoryQueryPort.findById(id, "Category not found.");
    }

    @Override
    public void save(Category category) {
        throwIfExistsByCode(category.getCode());
        assignIdIfAbsent(category);
        category.setPath(buildPath(category));
        categoryCommandPort.save(category);
    }

    private void assignIdIfAbsent(Category category) {
        if (category.getId() == null || category.getId().isBlank()) {
            category.setId(new ObjectId().toHexString());
        }
    }

    private List<String> buildPath(Category category) {
        if (category.getParentId() == null) {
            return List.of(category.getId());
        }

        Category parent = categoryQueryPort.findById(category.getParentId(), "Parent category not found.");

        List<String> path = new ArrayList<>(parent.getPath());
        path.add(category.getId());
        return path;
    }

    private void throwIfExistsByCode(String code) {
        if (categoryQueryPort.existsByCode(code)) {
            throw new CategoryAlreadyExistsException("Category already exists by code.");
        }
    }
}