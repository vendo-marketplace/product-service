package com.vendo.product_service.adapter.category.out.mapper;

import com.vendo.product_service.adapter.category.in.dto.CategoryResponse;
import com.vendo.product_service.adapter.category.in.dto.CreateCategoryRequest;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.infrastructure.mapper.MapStructConfig;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface DtoCategoryMapper {

    Category toCategory(CreateCategoryRequest request);
    CategoryResponse toResponse(Category category);

}
