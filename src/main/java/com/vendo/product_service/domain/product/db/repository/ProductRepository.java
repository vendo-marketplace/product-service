package com.vendo.product_service.domain.product.db.repository;

import com.vendo.product_service.domain.product.db.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
