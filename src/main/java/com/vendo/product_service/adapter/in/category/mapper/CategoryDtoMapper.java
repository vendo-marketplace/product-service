package com.vendo.product_service.adapter.in.category.mapper;

import com.vendo.product_service.infrastructure.config.MapStructConfig;
import com.vendo.product_service.adapter.in.category.dto.CategoryEntityResponse;
import com.vendo.product_service.adapter.in.category.dto.CreateCategoryRequest;
import com.vendo.product_service.domain.category.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface CategoryDtoMapper {

    CategoryEntityResponse toResponse(Category entity);

    Category toEntity(CreateCategoryRequest request);
}
