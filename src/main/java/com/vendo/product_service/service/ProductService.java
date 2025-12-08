package com.vendo.product_service.service;

import com.vendo.product_service.common.exception.ProductNotFoundException;
import com.vendo.product_service.common.mapper.ProductMapper;
import com.vendo.product_service.model.Product;
import com.vendo.product_service.repository.ProductRepository;
import com.vendo.product_service.web.dto.CreateProductRequest;
import com.vendo.product_service.web.dto.ProductResponse;
import com.vendo.product_service.web.dto.UpdateProductRequest;
import com.vendo.security.common.exception.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.vendo.product_service.security.common.helper.SecurityContextHelper.getUserIdFromContext;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    private final ProductRepository productRepository;

    public void save(CreateProductRequest createProductRequest) {
        Product product = productMapper.toProductFromCreateProductRequest(createProductRequest);

        product.setActive(true);
        product.setSellerId(getUserIdFromContext());

        productRepository.save(product);
    }

    public void update(String id, UpdateProductRequest updateProductRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found."));;

        if (product.getSellerId() != null && !product.getSellerId().equals(getUserIdFromContext())) {
            throw new AccessDeniedException("Only owners can edit their product.");
        }

        Optional.ofNullable(updateProductRequest.title()).ifPresent(product::setTitle);
        Optional.ofNullable(updateProductRequest.description()).ifPresent(product::setDescription);
        Optional.ofNullable(updateProductRequest.quantity()).ifPresent(product::setQuantity);
        Optional.ofNullable(updateProductRequest.price()).ifPresent(product::setPrice);
        Optional.ofNullable(updateProductRequest.active()).ifPresent(product::setActive);

        productRepository.save(product);
    }

    public ProductResponse findById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found."));

        return productMapper.toProductResponse(product);
    }

}
