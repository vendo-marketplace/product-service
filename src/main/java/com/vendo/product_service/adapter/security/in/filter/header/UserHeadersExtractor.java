package com.vendo.product_service.adapter.security.in.filter.header;

import com.vendo.product_service.domain.user.User;
import com.vendo.user_lib.type.UserStatus;
import com.vendo.utils_lib.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.vendo.core_lib.constants.Delimiters.COMMA_DELIMITER;
import static com.vendo.security_lib.type.UserHeaders.*;

@Component
public class UserHeadersExtractor {

    public User extract(HttpServletRequest request) {
        return User.builder()
                .id(require(request, ID.getHeader()))
                .email(request.getHeader(EMAIL.getHeader()))
                .status(extractStatus(request.getHeader(STATUS.getHeader())))
                .roles(extractRoles(request.getHeader(ROLES.getHeader())))
                .emailVerified(Boolean.parseBoolean(request.getHeader(EMAIL_VERIFIED.getHeader())))
                .build();
    }

    private String require(HttpServletRequest request, String header) {
        String value = request.getHeader(header);
        if (StringUtils.isEmpty(value)) {
            throw new AuthenticationCredentialsNotFoundException("Required user header %s is missing.".formatted(header));
        }
        return value;
    }

    private UserStatus extractStatus(String status) {
        try {
            return UserStatus.valueOf(status);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BadCredentialsException("Invalid status header: %s.".formatted(status));
        }
    }

    private List<String> extractRoles(String roles) {
        if (StringUtils.isEmpty(roles)) return List.of();

        return Arrays.stream(roles.split(COMMA_DELIMITER))
                .map(String::trim)
                .filter(role -> !role.isBlank())
                .toList();
    }
}