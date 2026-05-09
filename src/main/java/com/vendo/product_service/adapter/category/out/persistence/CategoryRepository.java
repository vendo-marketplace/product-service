package com.vendo.product_service.adapter.category.out.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface CategoryRepository extends MongoRepository<MongoCategory, String> {

    boolean existsByCode(String code);

}
