package com.vendo.product_service.application;


import com.vendo.product_service.adapter.model.product.MongoProduct;
import com.vendo.product_service.adapter.out.product.mapper.ProductEntityMapper;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.category.port.CategoryQueryPort;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.domain.product.port.ProductCommandPort;
import com.vendo.product_service.domain.product.port.ProductQueryPort;
import com.vendo.security.common.exception.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.vendo.product_service.security.common.helper.SecurityContextHelper.getUserIdFromContext;

@Component
@RequiredArgsConstructor
public class ProductService {

    private final ProductCommandPort commandPort;
    private final ProductQueryPort queryPort;
    private final CategoryQueryPort categoryQueryPort;
    private final ProductEntityMapper productEntityMapper;

    public void save(Product product) {
        if (product.getCategoryId() != null && !categoryQueryPort.existsById(product.getCategoryId())) {
            throw new CategoryNotFoundException("Category not found.");
        }

        commandPort.save(product);
    }

    public void update(String id, Product updatedProduct) {
        Product existing = queryPort.findById(id);

        if (!existing.getOwnerId().equals(getUserIdFromContext())) {
            throw new AccessDeniedException("Only owner can edit its product.");
        }

        if (updatedProduct.getCategoryId() != null &&
                !categoryQueryPort.existsById(updatedProduct.getCategoryId())) {
            throw new CategoryNotFoundException("Category not found.");
        }

        MongoProduct mongoProduct = productEntityMapper.toMongoEntity(updatedProduct);
        Product product = productEntityMapper.toEntity(mongoProduct);

        product.setId(existing.getId());
        product.setOwnerId(existing.getOwnerId());

        commandPort.save(product);
    }

    public Product findById(String id) {
        return queryPort.findById(id);
    }
}