package com.vendo.product_service.port.out.product;

import com.vendo.event_lib.product.ProductCreatedEvent;

public interface ProductCreatedSenderPort {

    void send(ProductCreatedEvent event);

}
