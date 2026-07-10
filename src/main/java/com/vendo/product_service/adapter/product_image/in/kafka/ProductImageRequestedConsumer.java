package com.vendo.product_service.adapter.product_image.in.kafka;

import com.vendo.event_lib.product.ProductImageRequestedEvent;
import com.vendo.product_service.adapter.product_image.out.mapper.ProductImageEventMapper;
import com.vendo.product_service.port.product_image.ProductImageUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageRequestedConsumer {

    private final ProductImageUseCase productImageUseCase;

    private final ProductImageEventMapper mapper;

    @KafkaListener(
            topics = "${kafka.events.product.image-requested-event.topic}",
            groupId = "${kafka.events.product.image-requested-event.groupId}",
            properties = {"auto.offset.reset: ${kafka.events.product.image-requested-event.properties.auto-offset-reset}"},
            containerFactory = "${kafka.events.product.image-requested-event.container-factory}"
    )
    private void listenProductImageRequestedEvent(ProductImageRequestedEvent event) {
        log.info("Received event for product image requested: {}", event);
        productImageUseCase.save(mapper.toProductImage(event));
    }

}
