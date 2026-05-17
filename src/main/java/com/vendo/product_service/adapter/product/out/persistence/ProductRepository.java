package com.vendo.product_service.adapter.product.out.persistence;

import org.springframework.data.domain.Limit;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<MongoProduct, String> {

    List<MongoProduct> findAllByOrderByIdDesc(Limit limit);

    List<MongoProduct> findByIdLessThanOrderByIdDesc(String id, Limit limit);

}
