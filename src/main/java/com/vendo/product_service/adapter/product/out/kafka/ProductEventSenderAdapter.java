package com.vendo.product_service.adapter.product.out.kafka;

import com.vendo.event_lib.product.AttributeEvent;
import com.vendo.event_lib.product.ProductCreatedEvent;
import com.vendo.event_lib.product.ProductUpdatedEvent;
import com.vendo.product_service.adapter.attribute.out.mapper.EventAttributeMapper;
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
    private final EventProductMapper productMapper;
    private final EventAttributeMapper attributeMapper;

    @Override
    public void sendCreated(Product product, List<Attribute> attributes) {
        List<AttributeEvent> attributeEvents = attributeMapper.toEvents(product.getAttributes(), attributes);
        ProductCreatedEvent event = productMapper.toCreatedEvent(product, attributeEvents);
        createdEventProducer.send(event);
    }

    @Override
    public void sendUpdated(Product product, List<Attribute> attributes) {
        List<AttributeEvent> attributeEvents = attributeMapper.toEvents(product.getAttributes(), attributes);
        ProductUpdatedEvent event = productMapper.toUpdatedEvent(product, attributeEvents);
        updatedEventProducer.send(event);
    }
}
