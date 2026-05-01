package com.vendo.product_service.adapter.category.out.mapper;

import com.vendo.product_service.adapter.category.in.dto.CategoryTreeResponse;
import com.vendo.product_service.application.category.model.CategoryNode;
import com.vendo.product_service.infrastructure.config.MapStructConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface CategoryTreeMapper {

    CategoryTreeResponse toResponse(CategoryNode node);

    List<CategoryTreeResponse> toResponseList(List<CategoryNode> nodes);
}