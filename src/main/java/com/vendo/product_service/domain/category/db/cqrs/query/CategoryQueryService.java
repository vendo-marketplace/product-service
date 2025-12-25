package com.vendo.product_service.domain.category.db.cqrs.query;

import com.vendo.product_service.domain.category.common.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.common.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.db.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public boolean existsById(String categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        return optionalCategory.isPresent();
    }

    public Optional<Category> findByTitle(String title) {
        return categoryRepository.findByTitleIgnoreCase(title);
    }

    public void throwIfExistsByTitle(String title) {
        Optional<Category> optionalCategory = findByTitle(title);

        if (optionalCategory.isPresent()) {
            throw new CategoryAlreadyExistsException("Category already exists.");
        }
    }
}
