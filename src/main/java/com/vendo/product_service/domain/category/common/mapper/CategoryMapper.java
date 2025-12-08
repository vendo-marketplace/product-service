package com.vendo.product_service.domain.category.common.mapper;

import com.vendo.product_service.common.config.MapStructConfig;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.web.dto.CategoriesResponse;
import com.vendo.product_service.domain.category.web.dto.CategoryRequest;
import com.vendo.product_service.domain.category.web.dto.CategoryResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface CategoryMapper {

    CategoryResponse toCategoryResponseFromCategory(Category category);

    CategoriesResponse toCategoriesResponseFromCategories(List<Category> categories);

    Category toCategoryFromCategoryRequest(CategoryRequest categoryRequest);

}
