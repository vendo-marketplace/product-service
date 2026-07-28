package com.vendo.product_service.infrastructure.shared.props;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductProps {

    @Value("${product.images.max-limit}")
    private int IMAGES_MAX_LIMIT;

    @Bean
    public int imagesMaxLimit() {
        return IMAGES_MAX_LIMIT;
    }

}
