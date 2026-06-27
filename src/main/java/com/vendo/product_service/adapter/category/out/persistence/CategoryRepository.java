package com.vendo.product_service.adapter.category.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<MongoCategory, String> {

    boolean existsBySlug(String slug);

}
