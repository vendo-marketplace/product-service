package com.vendo.product_service.domain.category.model.embedded;

import lombok.Data;

import java.util.List;

@Data
public class AttributeValue {

    private String type;

    private boolean required;

    private List<String> allowedValues;

}
