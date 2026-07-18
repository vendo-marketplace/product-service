package com.vendo.product_service.adapter.shared.out.http;

import com.vendo.product_service.adapter.shared.out.http.exception.HttpClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
class RestHttpClient implements HttpClient {

    private final RestTemplate restTemplate;

    @Override
    public void put(String url, Object body) {
        System.out.println(url);
        System.out.println(body);
        try {
            restTemplate.put(url, body);
        } catch (Exception e) {
            throw new HttpClientException("Http request failed. Reason: %s.".formatted(e.getMessage()));
        }
    }

}
