package com.vendo.product_service.adapter.attribute.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AttributeRepository extends MongoRepository<MongoAttribute, String> {
}
