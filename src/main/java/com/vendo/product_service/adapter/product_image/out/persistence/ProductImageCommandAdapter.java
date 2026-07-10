package com.vendo.product_service.adapter.product_image.out.persistence;

import com.vendo.product_service.adapter.product_image.out.mapper.ProductImageMapper;
import com.vendo.product_service.domain.product_image.exception.ProductImageAlreadyExists;
import com.vendo.product_service.domain.product_image.model.ProductImage;
import com.vendo.product_service.port.product_image.ProductImageCommandPort;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductImageCommandAdapter implements ProductImageCommandPort {

    private final ProductImageRepository repository;
    private final ProductImageMapper mapper;

    @Override
    public void save(ProductImage productImage) {
        try {
            repository.save(mapper.toEntity(productImage));
        } catch (DuplicateKeyException e) {
            throw new ProductImageAlreadyExists("Product image already exists by key: %s.".formatted(productImage.key()));
        }
    }
}
