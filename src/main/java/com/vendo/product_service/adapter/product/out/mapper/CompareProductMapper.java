package com.vendo.product_service.adapter.product.out.mapper;

import com.vendo.product_service.adapter.product.in.dto.CompareProductResponse;
import com.vendo.product_service.application.product.model.ProductComparison;
import com.vendo.product_service.infrastructure.config.mapper.MapStructConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface CompareProductMapper {

    List<CompareProductResponse> toResponses(List<ProductComparison> comparisons);

}
