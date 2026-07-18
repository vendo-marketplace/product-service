package com.vendo.product_service.adapter.shared.out.http;

import com.vendo.product_service.adapter.shared.out.http.exception.HttpClientException;

public interface HttpClient {

    void put(String url, String contentType, Object body) throws HttpClientException;

}
