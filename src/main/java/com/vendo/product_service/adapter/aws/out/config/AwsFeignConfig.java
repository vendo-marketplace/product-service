package com.vendo.product_service.adapter.aws.out.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.product_service.adapter.aws.out.exception.AwsServiceErrorDecoder;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AwsFeignConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public ErrorDecoder errorDecoder() {
        return new AwsServiceErrorDecoder(objectMapper);
    }

}
