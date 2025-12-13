package com.vendo.product_service.domain.category.common.mapper;

import com.vendo.product_service.common.config.MapStructConfig;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.web.dto.CategoriesResponse;
import com.vendo.product_service.domain.category.web.dto.CreateCategoryRequest;
import com.vendo.product_service.domain.category.web.dto.CategoryResponse;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface CategoryMapper {

    CategoryResponse toCategoryResponseFromCategory(Category category);

    default CategoriesResponse toCategoriesResponseFromCategories(List<Category> categories) {
        List<CategoryResponse> items = new ArrayList<>();

        for (Category category : categories) {
            items.add(toCategoryResponseFromCategory(category));
        }

        return CategoriesResponse.builder().items(items).build();
    }

    Category toCategoryFromCategoryRequest(CreateCategoryRequest createCategoryRequest);

}
