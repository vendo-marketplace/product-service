package com.vendo.product_service.adapter.image.out.kafka;

import com.vendo.product_service.port.image.ImageEventSenderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ImageEventSenderAdapter implements ImageEventSenderPort {

    private final FileDeletedEventProducer fileDeletedEventProducer;

    @Override
    public void delete(String imageKey) {
        fileDeletedEventProducer.send(imageKey);
    }
}
