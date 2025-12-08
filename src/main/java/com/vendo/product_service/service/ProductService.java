package com.vendo.product_service.service;

import com.vendo.product_service.common.exception.ProductAlreadyExistsException;
import com.vendo.product_service.common.mapper.ProductMapper;
import com.vendo.product_service.model.Product;
import com.vendo.product_service.repository.ProductRepository;
import com.vendo.product_service.web.dto.CreateProductRequest;
import com.vendo.product_service.web.dto.ProductResponse;
import com.vendo.product_service.web.dto.UpdateProductRequest;
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
        ProductResponse productResponse = findById(id);

        Optional.ofNullable(updateProductRequest.title()).ifPresent(productResponse::setTitle);
        Optional.ofNullable(updateProductRequest.description()).ifPresent(productResponse::setDescription);
        Optional.ofNullable(updateProductRequest.quantity()).ifPresent(productResponse::setQuantity);
        Optional.ofNullable(updateProductRequest.price()).ifPresent(productResponse::setPrice);
        Optional.ofNullable(updateProductRequest.active()).ifPresent(productResponse::setActive);

        productRepository.save(productMapper.toProductFromProductResponse(productResponse));
    }

    public ProductResponse findById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductAlreadyExistsException("Product already exists."));

        return productMapper.toProductResponse(product);
    }

}
