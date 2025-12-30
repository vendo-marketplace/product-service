package com.vendo.product_service.common.exception.handler;

public interface FieldNormalizer<T, R> {

    T normalize(R field);

}
