package com.vendo.product_service.adapter.server.in.exception.handler;

public interface FieldNormalizer<T, R> {

    T normalize(R field);

}
