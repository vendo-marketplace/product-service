package com.vendo.product_service.adapter.out.category.repository;

import com.vendo.product_service.adapter.model.category.MongoCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<MongoCategory, String> {

    boolean existsByCode(String code);

    Optional<MongoCategory> findByCodeIgnoreCase(String code);
}
