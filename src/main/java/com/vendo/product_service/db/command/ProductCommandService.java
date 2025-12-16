package com.vendo.product_service.db.command;

import com.vendo.product_service.common.exception.ProductNotFoundException;
import com.vendo.product_service.db.model.Product;
import com.vendo.product_service.db.repository.ProductRepository;
import com.vendo.security.common.exception.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.vendo.product_service.security.common.helper.SecurityContextHelper.getUserIdFromContext;

@Component
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductRepository productRepository;

    public void save(Product product) {
        productRepository.save(product);
    }

    public void update(String id, Product product) {
        Product productById = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found."));

        if (!productById.getOwnerId().equals(getUserIdFromContext())) {
            throw new AccessDeniedException("Only owners can edit their product.");
        }

        Optional.ofNullable(product.getTitle()).ifPresent(productById::setTitle);
        Optional.ofNullable(product.getDescription()).ifPresent(productById::setDescription);
        Optional.of(product.getQuantity()).ifPresent(productById::setQuantity);
        Optional.ofNullable(product.getPrice()).ifPresent(productById::setPrice);
        Optional.of(product.isActive()).ifPresent(productById::setActive);

        productRepository.save(product);
    }

}
