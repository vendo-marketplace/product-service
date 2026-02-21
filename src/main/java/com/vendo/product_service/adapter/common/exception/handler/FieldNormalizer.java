package com.vendo.product_service.adapter.common.exception.handler;

public interface FieldNormalizer<T, R> {

    T normalize(R field);

}
