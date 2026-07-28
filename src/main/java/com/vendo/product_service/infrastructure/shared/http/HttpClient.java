package com.vendo.product_service.infrastructure.shared.http;

import com.vendo.product_service.infrastructure.shared.http.exception.HttpClientException;

public interface HttpClient {

    void put(String url, String contentType, Object body) throws HttpClientException;

}
