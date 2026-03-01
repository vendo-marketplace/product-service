package com.vendo.product_service.adapter.out.category;

import com.vendo.product_service.adapter.out.category.mapper.CategoryMapper;
import com.vendo.product_service.adapter.out.category.repository.CategoryRepository;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.category.CategoryCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryCommandAdapter implements CategoryCommandPort {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public void save(Category category) {
        categoryRepository.save(categoryMapper.toMongoEntity(category));
    }
}
