package com.vendo.product_service.adapter.product_image.out.persistence;

import com.vendo.product_service.domain.product_image.model.ProductImageStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.Collection;
import java.util.List;

public interface ProductImageRepository extends MongoRepository<ProductImageMongo, String> {

    List<ProductImageMongo> findAllByKeyIn(Collection<String> keys);

    @Query("{ 'key': { $in: ?0 } }")
    @Update("{ '$set': { 'status': ?1 } }")
    void updateStatusByKeyIn(List<String> keys, ProductImageStatus status);

}
