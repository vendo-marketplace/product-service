package com.vendo.product_service.adapter.image.out.persistence;

import com.vendo.product_service.adapter.image.out.mapper.ProductImageMapper;
import com.vendo.product_service.domain.image.exception.ProductImageNotFoundException;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.port.image.ProductImageQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class ProductImageQueryAdapter implements ProductImageQueryPort {

    private final ProductImageMapper mapper;
    private final ProductImageRepository repository;

    @Override
    public List<Image> findAllBy(List<String> keys) {
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
