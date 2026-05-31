package com.vendo.product_service.adapter.security.in.filter;

import com.vendo.core_lib.type.ServiceName;
import com.vendo.core_lib.type.ServiceRole;
import com.vendo.product_service.adapter.security.out.jwt.parser.InternalTokenClaims;
import com.vendo.product_service.adapter.security.out.jwt.parser.TokenClaimsParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.vendo.security_lib.constants.AuthConstants.AUTHORIZATION_HEADER;

@Slf4j
@Component
@RequiredArgsConstructor
public class InternalGatewayFilter extends OncePerRequestFilter {

    private final TokenClaimsParser tokenClaimsParser;

    private final InternalAntPathResolver antPathResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = FilterUtils.getTokenFromRequest(request.getHeader(AUTHORIZATION_HEADER));
            InternalTokenClaims claims = validateClaims(token);
            FilterUtils.addAuthToContext(claims, claims.roles());
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new AuthenticationServiceException("Unauthorized.");
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return antPathResolver.isPermittedPath(requestURI);
    }

    private InternalTokenClaims validateClaims(String token) {
        InternalTokenClaims claims = tokenClaimsParser.extractInternal(token);

        boolean isProductService = claims.audience().contains(ServiceName.PRODUCT_SERVICE.toString());
        boolean hasInternalRole = claims.roles().contains(ServiceRole.INTERNAL.toString());

        if (!isProductService || !hasInternalRole) {
            log.error("Invalid token claims {}.", claims);
            throw new BadCredentialsException("Invalid token.");
        }

        return claims;
    }
}
