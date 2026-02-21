package com.vendo.product_service.adapter.out.category;

import com.vendo.product_service.adapter.out.category.mapper.CategoryEntityMapper;
import com.vendo.product_service.adapter.out.category.repository.CategoryRepository;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.port.CategoryCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryCommandAdapter implements CategoryCommandPort {
    private final CategoryRepository categoryRepository;
    private final CategoryEntityMapper categoryEntityMapper;


    @Override
    public void save(Category category) {
        categoryRepository.save(categoryEntityMapper.toCategoryEntityFromCategoryDomain(category));
    }
}
