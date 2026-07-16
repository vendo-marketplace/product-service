package com.vendo.product_service.infrastructure.config.feign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.vendo.product_service.adapter.aws.out")
public class OpenFeignConfig {
}
