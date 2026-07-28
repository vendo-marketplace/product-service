package com.vendo.product_service.adapter.category.out.persistence;

import org.springframework.data.repository.ListCrudRepository;

public interface CategoryRepository extends ListCrudRepository<MongoCategory, String> {

}
