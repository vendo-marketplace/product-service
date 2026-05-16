package com.vendo.product_service.adapter.attribute.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface AttributeRepository extends MongoRepository<MongoAttribute, String> {


}
