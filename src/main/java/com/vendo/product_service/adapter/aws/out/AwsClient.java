package com.vendo.product_service.adapter.aws.out;

import com.vendo.product_service.adapter.aws.out.config.AwsFeignConfig;
import com.vendo.product_service.adapter.aws.out.dto.ImageValidationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(
        name = "aws-service",
        path = "/internal/storage*",
        configuration = AwsFeignConfig.class)
public interface AwsClient {

    @PostMapping("/validate")
    void validate(@RequestBody ImageValidationRequest request);

}
