package com.vendo.product_service.infrastructure.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "endpoints.protected")
public class PathProps {

    private Set<String> general;
    private Set<String> internal;
    private Set<String> product;

    public String[] getAllPaths() {
        return Stream.of(general, internal, product)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .toArray(String[]::new);
    }

}
