package com.vendo.product_service.adapter.security.in.filter;

import com.vendo.product_service.infrastructure.props.PathProps;
import com.vendo.security_starter.resolver.AntPathResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class InternalAntPathResolver implements AntPathResolver {

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final PathProps pathProps;

    @Override
    public boolean isPermittedPath(String path) {
        Set<String> PERMITTED_PATHS = Stream.of(pathProps.getGeneral(), pathProps.getProduct())
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        return PERMITTED_PATHS.stream().anyMatch(pr -> antPathMatcher.match(pr, path));
    }
}
