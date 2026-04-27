package com.vendo.product_service.test_utils.builder;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.attribute.model.AttributeType;

public class AttributeDataBuilder {

    public static Attribute withAllFields() {
        return new Attribute("1", "Title", AttributeType.STRING, false, null);
    }

}
