package com.vendo.product_service.adapter.image.out.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageDeletedEventProducer {

    @Value("${kafka.events.product.image-deleted-event.topic}")
    private String productImageDeletedEventTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void send(String imageKey) {
        log.info("Sent event for product image deleted: {}.", imageKey);
        kafkaTemplate.send(productImageDeletedEventTopic, imageKey);
    }

}
