package com.vendo.product_service.adapter.product_image.out.persistence;

import com.vendo.product_service.adapter.product_image.out.mapper.ProductImageMapper;
import com.vendo.product_service.domain.product_image.exception.ProductImageNotFoundException;
import com.vendo.product_service.domain.product_image.model.ProductImage;
import com.vendo.product_service.port.product_image.ProductImageQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class ProductImageQueryAdapter implements ProductImageQueryPort {

    private final ProductImageMapper mapper;
    private final ProductImageRepository repository;

    @Override
    public List<ProductImage> findAllBy(List<String> keys) {
        List<ProductImageMongo> entities = repository.findAllByKeyIn(keys);
        requireAllExist(keys, entities.stream().map(ProductImageMongo::getKey).toList());
        return mapper.toProductImage(entities);
    }

    private void requireAllExist(List<String> requestKeys, List<String> entitiesKeys) {
        if (requestKeys.size() == entitiesKeys.size()) return;

        for (String requestKey : requestKeys) {
            if (!entitiesKeys.contains(requestKey)) {
                throw new ProductImageNotFoundException("Product image not found by key: %s.".formatted(requestKey));
            }
        }
    }

}
