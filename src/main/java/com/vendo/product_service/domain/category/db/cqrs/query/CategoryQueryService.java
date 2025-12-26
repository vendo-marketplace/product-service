package com.vendo.product_service.domain.category.db.cqrs.query;

import com.vendo.product_service.domain.category.common.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.common.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.db.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryQueryService {

    private final CategoryRepository categoryRepository;

    public Category findById(String id) {
        return findById(id, "Category not found.");
    }

    public Category findById(String id, String exceptionMessage) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(exceptionMessage));
    }

    public Category findByParentId(String parentId) {
         return categoryRepository.findByParentId((parentId))
                 .orElseThrow(() -> new CategoryNotFoundException("Category not found by parent."));
    }

    public boolean existsById(String categoryId) {
        return categoryRepository.existsById(categoryId);
    }

    public void throwExistsByCode(String code) {
        if (categoryRepository.existsByCode(code)) {
            throw new CategoryAlreadyExistsException("Category already exists by code.");
        }
    }

    public void throwIfExistsByTitle(String title) {
        if (categoryRepository.existsByTitle(title)) {
            throw new CategoryAlreadyExistsException("Category already exists by title.");
        }
    }
}
