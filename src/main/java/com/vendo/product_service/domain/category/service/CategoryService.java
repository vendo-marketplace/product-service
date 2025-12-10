package com.vendo.product_service.domain.category.service;

import com.vendo.product_service.domain.category.common.exception.CategoryAlreadyExistsException;
import com.vendo.product_service.domain.category.common.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.common.mapper.CategoryMapper;
import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.repository.CategoryRepository;
import com.vendo.product_service.domain.category.web.dto.CategoriesResponse;
import com.vendo.product_service.domain.category.web.dto.CategoryRequest;
import com.vendo.product_service.domain.category.web.dto.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    private final CategoryRepository categoryRepository;

    public void save(CategoryRequest categoryRequest) {
        Optional<Category> optionalCategory = findByTitle(categoryRequest.title());
        if (optionalCategory.isPresent()) {
            throw new CategoryAlreadyExistsException("Category already exists.");
        }

        Category category = categoryMapper.toCategoryFromCategoryRequest(categoryRequest);
        categoryRepository.save(category);
    }

    public CategoryResponse findById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found."));

        return categoryMapper.toCategoryResponseFromCategory(category);
    }

    public CategoriesResponse findAll() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toCategoriesResponseFromCategories(categories);
    }

    public Optional<Category> findByTitle(String title) {
        return categoryRepository.findByTitleIgnoreCase(title);
    }

    public CategoriesResponse findAllByType(CategoryType categoryType) {
        List<Category> categories = categoryRepository.findAllByCategoryType(categoryType);
        return categoryMapper.toCategoriesResponseFromCategories(categories);
    }
}
