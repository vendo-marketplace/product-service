package com.vendo.product_service.adapter.image.out.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileDeletedEventProducer {

    @Value("${kafka.events.file.deleted-event.topic}")
    private String fileDeletedEventTopic;

    private final KafkaTemplate<String, String> stringKafkaTemplate;

    public void send(String fileKey) {
        stringKafkaTemplate.send(fileDeletedEventTopic, fileKey);
        log.info("Sent event for file deleted: {}.", fileKey);
    }

}
