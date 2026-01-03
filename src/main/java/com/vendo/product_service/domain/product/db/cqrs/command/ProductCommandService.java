package com.vendo.product_service.domain.product.db.cqrs.command;

import com.vendo.product_service.domain.product.common.exception.ProductNotFoundException;
import com.vendo.product_service.domain.product.db.model.Product;
import com.vendo.product_service.domain.category.db.cqrs.query.CategoryQueryService;
import com.vendo.product_service.domain.product.db.repository.ProductRepository;
import com.vendo.security.common.exception.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.vendo.product_service.security.common.helper.SecurityContextHelper.getUserIdFromContext;

@Component
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductRepository productRepository;

    private final CategoryQueryService categoryQueryService;

    public void save(Product product) {
        productRepository.save(product);
    }

    public void update(String id, Product product) {
        Product productById = findProductByIdOrThrow(id);
        validateAuthenticatedUserAsProductOwner(productById.getOwnerId());

        Optional.ofNullable(product.getTitle()).ifPresent(productById::setTitle);
        Optional.ofNullable(product.getDescription()).ifPresent(productById::setDescription);
        Optional.ofNullable(product.getQuantity()).ifPresent(productById::setQuantity);
        Optional.ofNullable(product.getPrice()).ifPresent(productById::setPrice);
        Optional.ofNullable(product.getAttributes()).ifPresent(productById::setAttributes);
        Optional.ofNullable(product.getActive()).ifPresent(productById::setActive);

        if (product.getCategoryId() != null && categoryQueryService.existsById(product.getCategoryId())) {
            productById.setCategoryId(product.getCategoryId());
        }

        productRepository.save(productById);
    }

    private Product findProductByIdOrThrow(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found."));
    }

    private void validateAuthenticatedUserAsProductOwner(String ownerId) {
        if (!ownerId.equals(getUserIdFromContext())) {
            throw new AccessDeniedException("Only owner can edit its product.");
        }
    }
}
