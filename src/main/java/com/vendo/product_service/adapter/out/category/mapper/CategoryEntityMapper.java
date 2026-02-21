package com.vendo.product_service.adapter.out.category.mapper;

import com.vendo.product_service.adapter.model.category.CategoryEntity;
import com.vendo.product_service.adapter.common.config.MapStructConfig;
import com.vendo.product_service.domain.category.model.Category;
import org.mapstruct.Mapper;

@Mapper(config = MapStructConfig.class)
public interface CategoryEntityMapper {
    Category toCategoryDomainFromCategoryEntity(CategoryEntity entity);

    CategoryEntity toCategoryEntityFromCategoryDomain(Category domain);

}
