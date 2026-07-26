package com.vendo.product_service.port.product;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.product.model.Product;

import java.util.List;

public interface ProductEventSenderPort {

    void sendCreated(Product product, List<Attribute> attributes);

    void sendUpdated(Product product, List<Attribute> attributes);

    void sendUpdated(Product product);

}
