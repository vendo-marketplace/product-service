package com.vendo.product_service.adapter.category.out.persistence;

import com.vendo.product_service.adapter.category.out.mapper.MongoCategoryMapper;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryQueryAdapter implements CategoryQueryPort {

    private final MongoCategoryMapper mapper;
    private final CategoryRepository repository;

    @Override
    public Category findById(String id, String message) {
        return repository.findById(id).map(mapper::toCategory)
                .orElseThrow(() -> new CategoryNotFoundException(message.isBlank() ? "Category not found." : message));
    }

    @Override
    public boolean existsById(String categoryId) {
        return repository.existsById(categoryId);
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.existsByCode(code);
    }
}
