package com.vendo.product_service.adapter.product_image.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductImageRepository extends MongoRepository<ProductImageMongo, String> {
}
