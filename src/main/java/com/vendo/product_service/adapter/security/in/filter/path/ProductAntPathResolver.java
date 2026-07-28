package com.vendo.product_service.adapter.security.in.filter.path;

import com.vendo.product_service.infrastructure.shared.props.PathProps;
import com.vendo.security_lib.resolver.AntPathResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductAntPathResolver implements AntPathResolver {

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final PathProps pathProps;

    @Override
    public boolean isPermittedPath(String path) {
        Set<String> PERMITTED_PATHS = Arrays.stream(pathProps.getAllPaths()).collect(Collectors.toSet());
        return PERMITTED_PATHS.stream().anyMatch(pr -> antPathMatcher.match(pr, path));
    }
}
