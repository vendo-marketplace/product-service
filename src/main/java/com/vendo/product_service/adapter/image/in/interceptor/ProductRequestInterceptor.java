package com.vendo.product_service.adapter.image.in.interceptor;

import com.vendo.product_service.adapter.security.out.internal.InternalTokenGenerationPort;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.vendo.security_lib.http.HttpUtils.AUTHORIZATION_HEADER;
import static com.vendo.security_lib.http.HttpUtils.BEARER_PREFIX;

@Configuration
@RequiredArgsConstructor
public class ProductRequestInterceptor {

    private final InternalTokenGenerationPort internalTokenGenerationPort;

    @Bean
    RequestInterceptor internalAwsRequestInterceptor() {
        return request -> request.header(
                AUTHORIZATION_HEADER,
                BEARER_PREFIX + internalTokenGenerationPort.generate()
        );
    }

}
