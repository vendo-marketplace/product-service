package com.vendo.product_service.domain.category.db.repository;

import com.vendo.product_service.domain.category.db.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {

    Optional<Category> findByTitleIgnoreCase(String title);

}
