package com.vendo.product_service.adapter.category.out.mapper;

import com.vendo.product_service.adapter.category.in.dto.CategoryTreeResponse;
import com.vendo.product_service.application.category.model.CategoryView;
import com.vendo.product_service.infrastructure.config.mapstruct.MapStructConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface CategoryTreeMapper {

    List<CategoryTreeResponse> toResponse(List<CategoryView> views);
}