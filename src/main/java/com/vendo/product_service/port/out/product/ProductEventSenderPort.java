package com.vendo.product_service.port.out.product;

import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.product.model.Product;
import jakarta.annotation.Nullable;

import java.util.List;

public interface ProductEventSenderPort {

    void sendCreated(Product product);

    void sendUpdated(Product product, @Nullable List<Attribute> attributes);

}
