package com.vendo.product_service.adapter.category.out.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "redis.category")
public class CacheCategoryNamespace extends CategoryNamespace {
}
