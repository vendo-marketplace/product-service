package com.vendo.product_service.shared.exception.formatter;

public interface FieldNormalizer<T, R> {

    T normalize(R field);

}
