package com.vendo.product_service.domain.category.service;

import com.vendo.product_service.domain.category.common.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.common.mapper.CategoryMapper;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.repository.CategoryRepository;
import com.vendo.product_service.domain.category.web.dto.CategoriesResponse;
import com.vendo.product_service.domain.category.web.dto.CategoryRequest;
import com.vendo.product_service.domain.category.web.dto.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    private final CategoryRepository categoryRepository;

    public void save(CategoryRequest categoryRequest) {
        categoryRepository.save(categoryMapper.toCategoryFromCategoryRequest(categoryRequest));
    }

    public CategoryResponse findById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found."));

        return categoryMapper.toCategoryResponseFromCategory(category);
    }

    public CategoryResponse findByTitle(String title) {
        Category category = categoryRepository.findByTitle(title)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found."));

        return categoryMapper.toCategoryResponseFromCategory(category);
    }

    public CategoriesResponse findAll() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toCategoriesResponseFromCategories(categories);
    }

}
