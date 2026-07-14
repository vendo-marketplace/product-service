package com.vendo.product_service.adapter.aws.out.config;

import com.vendo.product_service.adapter.aws.out.exception.AwsServiceErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsFeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new AwsServiceErrorDecoder();
    }

}
