package com.vendo.product_service.adapter.product.out.kafka;

import com.vendo.event_lib.product.ProductUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductUpdatedEventProducer {

    @Value("${kafka.events.product.updated-event.topic}")
    private String productUpdatedEventTopic;

    private final KafkaTemplate<String, ProductUpdatedEvent> kafkaTemplate;

    public void send(ProductUpdatedEvent event) {
        log.info("Sent event for product updated: {}.", event);
        kafkaTemplate.send(productUpdatedEventTopic, event);
    }

}
