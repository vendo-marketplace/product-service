package com.vendo.product_service.domain.category.db.query;

import com.vendo.product_service.domain.category.common.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.db.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryQueryService {

    private final CategoryRepository categoryRepository;

    public Category findByIdOrThrow(String id) {
        return findByIdOrThrow(id, "Category not found.");
    }

    public Category findByIdOrThrow(String id, String exceptionMessage) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(exceptionMessage));
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findByTitle(String title) {
        return categoryRepository.findByTitleIgnoreCase(title);
    }

    public List<Category> findAllByType(CategoryType categoryType) {
        return categoryRepository.findAllByCategoryType(categoryType);
    }

}
