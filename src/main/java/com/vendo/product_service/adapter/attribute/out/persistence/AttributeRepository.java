package com.vendo.product_service.adapter.attribute.out.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface AttributeRepository extends CrudRepository<MongoAttribute, String> {

    List<MongoAttribute> findAllByIdIsIn(Collection<String> ids);

}
