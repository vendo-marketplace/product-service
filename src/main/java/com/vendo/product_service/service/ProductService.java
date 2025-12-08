package com.vendo.product_service.service;

import com.vendo.product_service.model.Product;
import com.vendo.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public void save(Product product) {
        productRepository.save(product);
    }

    public void update() {

    }

    public void findById(String id) {

    }

}
