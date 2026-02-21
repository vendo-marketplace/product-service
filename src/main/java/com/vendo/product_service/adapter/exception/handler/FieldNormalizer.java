package com.vendo.product_service.adapter.exception.handler;

public interface FieldNormalizer<T, R> {

    T normalize(R field);

}
