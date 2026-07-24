package com.vendo.product_service.adapter.image.out.mapper;

import com.vendo.product_service.adapter.aws.out.dto.nested.PresignBody;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.infrastructure.config.mapper.MapStructConfig;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(config = MapStructConfig.class)
public interface PresignMapper {

    List<PresignBody> toPresignBodies (List<Image> images);

}
