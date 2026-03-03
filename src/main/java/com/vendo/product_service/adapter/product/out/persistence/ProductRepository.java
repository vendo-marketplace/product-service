package com.vendo.product_service.adapter.product.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<MongoProduct, String> {
}
