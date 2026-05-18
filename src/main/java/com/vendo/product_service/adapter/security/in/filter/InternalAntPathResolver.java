package com.vendo.product_service.adapter.security.in.filter;

import com.vendo.security_lib.resolver.AntPathResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Arrays;

@Component
public class InternalAntPathResolver implements AntPathResolver {

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public static final String[] INTERNAL_PATHS = new String[] {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/health",
            "/categories/tree",
    };

    @Override
    public boolean isPermittedPath(String path) {
        return Arrays.stream(INTERNAL_PATHS).anyMatch(pr -> antPathMatcher.match(pr, path));
    }
}
