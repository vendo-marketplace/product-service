package com.vendo.product_service.adapter.category.out.persistence;

import com.vendo.product_service.adapter.category.out.mapper.MongoCategoryMapper;
import com.vendo.product_service.domain.category.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.category.CategoryCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class CategoryCommandAdapter implements CategoryCommandPort {

    private final MongoCategoryMapper mapper;
    private final CategoryRepository repository;

    @Override
    public void save(Category category) {
        try {
            repository.save(mapper.toEntity(category));
        } catch (DuplicateKeyException e) {
            throw new CategoryAlreadyExistsException("Category already exists by slug.");
        }
    }
}
