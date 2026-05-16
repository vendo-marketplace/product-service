package com.vendo.product_service.adapter.product.out.persistence;

import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface ProductRepository extends MongoRepository<MongoProduct, String> {

    List<MongoProduct> getAllByCreatedAtOrderByCreatedAtDesc(Instant createdAt, Limit limit);

    List<MongoProduct> findAllByOrderByCreatedAtDesc(Limit limit);

}
