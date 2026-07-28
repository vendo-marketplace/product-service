package com.vendo.product_service.infrastructure.config.feign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.vendo.product_service.adapter.image.out.aws")
public class OpenFeignConfig {
}
