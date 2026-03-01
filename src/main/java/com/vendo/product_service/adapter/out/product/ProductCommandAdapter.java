package com.vendo.product_service.adapter.out.product;

import com.vendo.product_service.adapter.model.product.MongoProduct;
import com.vendo.product_service.adapter.out.product.mapper.ProductMapper;
import com.vendo.product_service.adapter.out.product.repository.ProductRepository;
import com.vendo.product_service.domain.category.exception.CategoryNotFoundException;
import com.vendo.product_service.domain.product.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.category.CategoryQueryPort;
import com.vendo.product_service.port.product.ProductCommandPort;
import com.vendo.security.common.exception.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.vendo.product_service.adapter.security.common.helper.SecurityContextHelper.getUserIdFromContext;

@Component
@RequiredArgsConstructor
public class ProductCommandAdapter implements ProductCommandPort {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryQueryPort categoryQueryPort;

    @Override
    public void save(Product product) {
        MongoProduct entity = productMapper.toMongoProduct(product);
        productRepository.save(entity);
    }

    @Override
    public void update(String id, Product product) {
        Product existing = findByIdOrThrow(id);

        throwIfNotOwnerOfProduct(existing.getOwnerId());
        throwIfCategoryNotFound(product.getCategoryId());

        product.setId(id);
        productRepository.save(productMapper.toMongoProduct(product));
    }

    private Product findByIdOrThrow(String id) {
        MongoProduct entity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found."));
        return productMapper.toProduct(entity);
    }

    private void throwIfNotOwnerOfProduct(String ownerId) {
        if (!ownerId.equals(getUserIdFromContext())) {
            throw new AccessDeniedException("Only owner can edit its product.");
        }
    }

    private void throwIfCategoryNotFound(String id) {
        if (id != null && !categoryQueryPort.existsById(id)) {
            throw new CategoryNotFoundException("Category not found.");
        }
    }
}