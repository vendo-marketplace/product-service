package com.vendo.product_service.adapter.category.out.persistence;

import com.vendo.product_service.adapter.category.out.mapper.MongoCategoryMapper;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.port.out.category.CategoryQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategoryQueryAdapter implements CategoryQueryPort {

    private final MongoCategoryMapper mapper;
    private final CategoryRepository repository;

    @Override
    public Category findById(String id, String message) {
        return repository.findById(id)
                .map(mapper::toCategory)
                .orElseThrow(() -> new CategoryNotFoundException(message));
    }

    @Override
    public Category findById(String id) {
        return findById(id, "Product not found.");
    }

    @Override
    public boolean existsById(String categoryId) {
        return repository.existsById(categoryId);
    }

    @Override
    public List<Category> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toCategory)
                .toList();
    }

}
