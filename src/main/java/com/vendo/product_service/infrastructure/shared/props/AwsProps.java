package com.vendo.product_service.infrastructure.shared.props;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsProps {

    @Value("${aws.base-url}")
    private String BASE_URL;

    @Bean
    public String baseUrl() {
        return BASE_URL;
    }

}
