package com.vendo.product_service.adapter.category.out.persistence;

import com.vendo.product_service.adapter.category.out.mapper.MongoCategoryMapper;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.category.CategoryCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryCommandAdapter implements CategoryCommandPort {

    private final MongoCategoryMapper mapper;
    private final CategoryRepository repository;

    @Override
    public void save(Category category) {
        repository.save(mapper.toEntity(category));
    }
}
