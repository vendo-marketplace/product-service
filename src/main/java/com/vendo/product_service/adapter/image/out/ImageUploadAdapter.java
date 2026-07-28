package com.vendo.product_service.adapter.image.out;

import com.vendo.product_service.infrastructure.shared.http.HttpClient;
import com.vendo.product_service.infrastructure.shared.http.exception.HttpClientException;
import com.vendo.product_service.domain.image.exception.ImageUploadException;
import com.vendo.product_service.domain.image.model.Image;
import com.vendo.product_service.port.image.ImageUploadPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageUploadAdapter implements ImageUploadPort {

    private final HttpClient httpClient;

    @Override
    public void upload(Map<String, Image> images) {
        try {
            for (Map.Entry<String, Image> entry : images.entrySet()) {
                Image value = entry.getValue();
                httpClient.put(entry.getKey(), value.contentType(), value.bytes());
            }
        } catch (HttpClientException e) {
            log.error("Unable to upload image: {}.", e.getMessage());
            throw new ImageUploadException("Unable to upload image.");
        }
    }

}
