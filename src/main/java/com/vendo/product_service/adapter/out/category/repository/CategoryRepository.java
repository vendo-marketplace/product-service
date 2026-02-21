package com.vendo.product_service.adapter.out.category.repository;

import com.vendo.product_service.adapter.model.category.CategoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<CategoryEntity, String> {

    boolean existsByCode(String code);

    Optional<CategoryEntity> findByCodeIgnoreCase(String code);
}
