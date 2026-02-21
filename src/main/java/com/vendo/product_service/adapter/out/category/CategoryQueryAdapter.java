package com.vendo.product_service.adapter.out.category;

import com.vendo.product_service.adapter.out.category.mapper.CategoryEntityMapper;
import com.vendo.product_service.adapter.out.category.repository.CategoryRepository;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.port.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryQueryAdapter implements CategoryQueryPort {
    private final CategoryEntityMapper categoryEntityMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public Category findById(String id, String s) {
        return categoryRepository.findById(id).map(categoryEntityMapper::toCategoryDomainFromCategoryEntity)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found."));
    }

    @Override
    public boolean existsById(String categoryId) {
        return categoryRepository.existsById(categoryId);
    }

    @Override
    public boolean existsByCode(String code) {
        return categoryRepository.existsByCode(code);
    }
}
