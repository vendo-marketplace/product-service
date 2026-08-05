package com.vendo.product_service.adapter.category.out.persistence;

import com.vendo.product_service.adapter.category.out.mapper.MongoCategoryMapper;
import com.vendo.product_service.adapter.product.out.persistence.MongoProduct;
import com.vendo.product_service.domain.category.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryImageType;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
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

    @Override
    public void update(String id, Category category) {
        MongoCategory entity = findOrThrow(id);
        mapper.updateEntity(entity, category);
        repository.save(entity);
    }

    @Override
    public void removeImage(String id, CategoryImageType type) {
        MongoCategory entity = findOrThrow(id);

        switch (type) {
            case ICON -> entity.setIconKey(null);
            case PREVIEW -> entity.setPreviewKey(null);
        }

        repository.save(entity);
    }

    private MongoCategory findOrThrow(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found."));
    }
}
