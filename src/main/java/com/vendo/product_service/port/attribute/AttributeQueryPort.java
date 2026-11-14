package com.vendo.product_service.port.attribute;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeFilter;

import java.util.List;

public interface AttributeQueryPort {

    Attribute findById(String id);

    List<Attribute> findAllByIds(List<String> ids);

    List<Attribute> findAll(AttributeFilter filter);
}
