package com.vendo.product_service.adapter.security.in.filter.path;

import com.vendo.security_starter.path.PathGroup;
import com.vendo.security_starter.path.PathProps;
import com.vendo.security_lib.resolver.AntPathResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class InternalAntPathResolver implements AntPathResolver {

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final PathProps pathProps;

    @Override
    public boolean isPermittedPath(String path) {
        return Arrays.stream(pathProps.paths(PathGroup.GENERAL, PathGroup.PRODUCT)).anyMatch(pr -> antPathMatcher.match(pr, path));
    }
}
