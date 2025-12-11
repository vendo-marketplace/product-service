package com.vendo.product_service.domain.category.db.repository;

import com.vendo.product_service.domain.category.common.type.CategoryType;
import com.vendo.product_service.domain.category.db.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends MongoRepository<Category, String> {

    List<Category> findAllByCategoryType(CategoryType categoryType);

    Optional<Category> findByTitleIgnoreCase(String title);
}
