package com.vendo.product_service.domain.category.common.mapper;

import com.vendo.product_service.common.config.MapStructConfig;
import com.vendo.product_service.domain.category.db.model.Category;
import com.vendo.product_service.domain.category.web.dto.CreateCategoryRequest;
import com.vendo.product_service.domain.category.web.dto.CategoryResponse;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface CategoryMapper {

    CategoryResponse toCategoryResponseFromCategory(Category category);

    Category toCategoryFromCategoryRequest(CreateCategoryRequest createCategoryRequest);

}
