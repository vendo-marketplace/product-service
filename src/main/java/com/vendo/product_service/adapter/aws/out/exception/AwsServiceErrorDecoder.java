package com.vendo.product_service.adapter.aws.out.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.core_lib.type.ServiceName;
import com.vendo.security_lib.exception.ExceptionResponse;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsServiceErrorDecoder implements ErrorDecoder {

    private final ObjectMapper mapper;

    @Override
    public Exception decode(String s, Response response) {

        if (HttpStatus.valueOf(response.status()).is5xxServerError()) {
            return new AwsServiceUnavailableException(ServiceName.AWS_SERVICE + " is unavailable.");
        }

        if (HttpStatus.BAD_REQUEST.value() == response.status()) {
           return handleBadRequest(response);
        }

        if (HttpStatus.CONFLICT.value() == response.status()) {
            log.error("Internal call failed: {}.", response.reason());
            throw new AwsInternalServerException("Something went wrong.");
        }

        log.error(response.toString());
        return new IllegalArgumentException("Unhandled aws exception.");
    }

    private Exception handleBadRequest(Response response) {
        try (InputStream io = response.body().asInputStream()) {
            ExceptionResponse exceptionResponse = mapper.readValue(io, ExceptionResponse.class);
            return new AwsBadRequestException(exceptionResponse);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new AwsInternalServerException("Something went wrong.");
        }
    }
}
