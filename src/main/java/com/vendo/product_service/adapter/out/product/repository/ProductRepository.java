package com.vendo.product_service.adapter.out.product.repository;

import com.vendo.product_service.adapter.model.product.MongoProduct;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<MongoProduct, String> {
}
