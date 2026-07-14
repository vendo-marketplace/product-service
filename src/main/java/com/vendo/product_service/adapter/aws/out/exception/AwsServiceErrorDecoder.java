package com.vendo.product_service.adapter.aws.out.exception;

import com.vendo.core_lib.type.ServiceName;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AwsServiceErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {

        if (HttpStatus.valueOf(response.status()).is5xxServerError()) {
            return new AwsServiceUnavailableException(ServiceName.AWS_SERVICE + " is unavailable.");
        }

        if (HttpStatus.CONFLICT.value() == response.status()) {
        }

        log.error(response.toString());
        return new IllegalArgumentException("Unhandled aws exception.");
    }

}
