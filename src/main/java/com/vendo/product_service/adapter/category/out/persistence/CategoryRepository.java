package com.vendo.product_service.adapter.category.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<MongoCategory, String> {

    boolean existsByCode(String code);

    Optional<MongoCategory> findByCodeIgnoreCase(String code);
}
