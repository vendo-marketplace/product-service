package com.vendo.product_service.security.common.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vendo.common.exception.ExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException {
        log.warn("Handling access denied exception: ", exception);

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                .code(HttpServletResponse.SC_FORBIDDEN)
                .path(request.getRequestURI())
                .message("You do not have permission to access this resource.")
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(exceptionResponse));
    }

}
