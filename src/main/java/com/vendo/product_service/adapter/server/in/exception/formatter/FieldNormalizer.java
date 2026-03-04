package com.vendo.product_service.adapter.server.in.exception.formatter;

public interface FieldNormalizer<T, R> {

    T normalize(R field);

}
