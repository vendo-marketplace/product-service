package com.vendo.product_service.service;

import com.vendo.product_service.common.exception.ProductAlreadyExistsException;
import com.vendo.product_service.model.Product;
import com.vendo.product_service.repository.ProductRepository;
import com.vendo.product_service.web.dto.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public void save(Product product) {
        productRepository.save(product);
    }

    public void update(String id, UpdateProductRequest updateProductRequest) {
        Product product = findById(id);

        Optional.ofNullable(updateProductRequest.title()).ifPresent(product::setTitle);
        Optional.ofNullable(updateProductRequest.description()).ifPresent(product::setDescription);
        Optional.ofNullable(updateProductRequest.quantity()).ifPresent(product::setQuantity);
        Optional.ofNullable(updateProductRequest.price()).ifPresent(product::setPrice);
        Optional.ofNullable(updateProductRequest.active()).ifPresent(product::setActive);

        productRepository.save(product);
    }

    public Product findById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductAlreadyExistsException("Product already exists."));
    }

}
