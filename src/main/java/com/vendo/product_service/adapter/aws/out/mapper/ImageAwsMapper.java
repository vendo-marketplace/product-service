package com.vendo.product_service.adapter.aws.out.mapper;

import com.vendo.product_service.adapter.aws.out.dto.ImageValidationRequest;
import com.vendo.product_service.domain.product_image.model.ProductImage;
import com.vendo.product_service.infrastructure.config.mapper.MapStructConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface ImageAwsMapper {

    List<ImageValidationRequest.ImageRequest> toRequest(List<ProductImage> productImages);

}
