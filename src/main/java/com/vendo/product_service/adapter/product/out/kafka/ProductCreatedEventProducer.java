package com.vendo.product_service.adapter.product.out.kafka;

import com.vendo.event_lib.product.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCreatedEventProducer {

    @Value("${kafka.events.product.created-event.topic}")
    private String productCreatedEventTopic;

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    public void send(ProductCreatedEvent event) {
        log.info("Sent event for product created: {}.", event);
        kafkaTemplate.send(productCreatedEventTopic, event);
    }
}
