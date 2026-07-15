package com.vendo.product_service.adapter.image.out.persistence;

import com.vendo.product_service.adapter.image.out.mapper.ProductImageMapper;
import com.vendo.product_service.domain.image.exception.ImageAlreadyExists;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.domain.image.model.ProductImageStatus;
import com.vendo.product_service.port.image.ImageCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class ImageCommandAdapter implements ImageCommandPort {
    private final ProductImageRepository repository;
    private final ProductImageMapper mapper;

    @Override
    public void save(Image image) {
        try {
            repository.save(mapper.toEntity(image));
        } catch (DuplicateKeyException e) {
            throw new ImageAlreadyExists("Product image already exists by key: %s.".formatted(image.key()));
        }
    }

    @Override
    public void updateAllBy(List<String> keys, ProductImageStatus status) {
        repository.updateStatusByKeyIn(keys, status);
    }

}
