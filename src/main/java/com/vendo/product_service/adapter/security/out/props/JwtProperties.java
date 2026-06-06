package com.vendo.product_service.adapter.security.out.props;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Accessors(fluent = true)
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    private Internal internal;

    public record Internal(String key) { }

}
