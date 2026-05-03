package com.vendo.product_service.adapter.product.out.kafka;

import com.vendo.event_lib.product.ProductCreatedEvent;
import com.vendo.event_lib.product.ProductUpdatedEvent;
import com.vendo.product_service.adapter.product.out.mapper.EventProductMapper;
import com.vendo.product_service.domain.attribute.model.Attribute;
import com.vendo.product_service.domain.product.model.Product;
import com.vendo.product_service.port.out.product.ProductEventSenderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductEventSenderAdapter implements ProductEventSenderPort {

    private final ProductCreatedEventProducer createdEventProducer;
    private final ProductUpdatedEventProducer updatedEventProducer;
    private final EventProductMapper mapper;

    @Override
    public void sendCreated(Product product, List<Attribute> attributes) {
        ProductCreatedEvent event = mapper.toCreatedEvent(product, attributes);
        createdEventProducer.send(event);
    }

    @Override
    public void sendUpdated(Product product, List<Attribute> attributes) {
        ProductUpdatedEvent event = mapper.toUpdatedEvent(product, attributes);
        updatedEventProducer.send(event);
    }
}
