package com.vendo.product_service.port.out.attribute;

import com.vendo.product_service.domain.attribute.model.Attribute;

public interface AttributeCommandPort {

    void save(Attribute attribute);

}
