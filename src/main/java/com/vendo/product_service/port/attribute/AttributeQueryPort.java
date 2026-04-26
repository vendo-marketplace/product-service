package com.vendo.product_service.port.attribute;

import com.vendo.product_service.domain.attribute.model.Attribute;

import java.util.List;

public interface AttributeQueryPort {

    Attribute findById(String id);

    List<Attribute> findAllByIdsIn(List<String> ids);

}
