package com.vendo.product_service.domain.category.repository;

import com.vendo.product_service.domain.category.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {

    Optional<Category> findByTitle(String title);

}
