package com.vendo.product_service.adapter.favorite.out.mapper;

import com.vendo.product_service.adapter.favorite.in.dto.FavoriteResponse;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.infrastructure.config.mapper.MapStructConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

@Mapper(config = MapStructConfig.class)
public abstract class DtoFavoriteMapper {

    @Value("${aws.base-url}")
    private String AWS_BASE_URL;

    @Mapping(source = "imageKeys", target = "images", qualifiedByName = "toImages")
    public abstract FavoriteResponse toResponse(Product product);

    public abstract List<FavoriteResponse> toResponses(List<Product> products);

    @Named("toImages")
    protected List<String> toImages(List<String> imageKeys) {
        List<String> images = new ArrayList<>();

        for (String imageKey : imageKeys) {
            images.add(AWS_BASE_URL.concat(imageKey));
        }

        return images;
    }
}
