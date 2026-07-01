package com.vendo.product_service.adapter.category.out.mapper;

import com.vendo.product_service.adapter.category.in.dto.CategoryResponse;
import com.vendo.product_service.adapter.category.in.dto.CreateCategoryRequest;
import com.vendo.product_service.domain.category.model.Category;
import com.vendo.product_service.domain.category.type.CategoryType;
import com.vendo.product_service.infrastructure.config.mapper.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapStructConfig.class)
public interface DtoCategoryMapper {

    Category toCategory(CreateCategoryRequest request);

    @Mapping(target = "type", source = ".", qualifiedByName = "toCategoryType")
    CategoryResponse toResponse(Category category);

    @Named("toCategoryType")
    default CategoryType toCategoryType(Category category) {
        return category.getType();
    }

}
