package com.vendo.product_service.common.mapper;

import com.vendo.product_service.common.config.MapStructConfig;
import com.vendo.product_service.db.model.Category;
import com.vendo.product_service.web.dto.CreateCategoryRequest;
import com.vendo.product_service.web.dto.CategoryResponse;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface CategoryMapper {

    CategoryResponse toCategoryResponseFromCategory(Category category);

    Category toCategoryFromCategoryRequest(CreateCategoryRequest createCategoryRequest);

}
