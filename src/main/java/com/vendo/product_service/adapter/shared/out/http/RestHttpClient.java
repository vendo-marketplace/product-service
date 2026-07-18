package com.vendo.product_service.adapter.shared.out.http;

import com.vendo.product_service.adapter.shared.out.http.exception.HttpClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
class RestHttpClient implements HttpClient {

    private final RestTemplate restTemplate;

    @Override
    public void put(String url, String contentType, Object body) {
        try {
            URI uri = URI.create(url);
            RequestEntity<byte[]> request = RequestEntity
                    .put(uri)
                    .contentType(MediaType.parseMediaType(contentType))
                    .body((byte[]) body);

            restTemplate.exchange(request, Void.class);
        } catch (Exception e) {
            throw new HttpClientException("Http request failed. Reason: %s.".formatted(e.getMessage()));
        }
    }

}
