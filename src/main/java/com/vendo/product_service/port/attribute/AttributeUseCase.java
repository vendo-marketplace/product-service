package com.vendo.product_service.port.attribute;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeFilter;

import java.util.List;

public interface AttributeUseCase {

    Attribute save(Attribute attribute);
    Attribute update(String id, Attribute attribute);

    List<Attribute> findAll(AttributeFilter filter);

}
