package com.vendo.product_service.adapter.category.in.mapper;

import com.vendo.product_service.adapter.category.in.dto.CategoryEntityResponse;
import com.vendo.product_service.adapter.category.in.dto.CreateCategoryRequest;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface CategoryMapper {

    CategoryEntityResponse toResponse(Category entity);

    Category toEntity(CreateCategoryRequest request);
}
