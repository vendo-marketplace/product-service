package com.vendo.product_service.adapter.category.out.persistence;

import com.vendo.product_service.adapter.category.out.mapper.MongoCategoryMapper;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.category.CategoryCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryCommandAdapter implements CategoryCommandPort {

    private final MongoCategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public void save(Category category) {
        categoryRepository.save(categoryMapper.toMongoEntity(category));
    }
}
