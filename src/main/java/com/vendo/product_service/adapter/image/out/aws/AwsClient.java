package com.vendo.product_service.adapter.image.out.aws;

import com.vendo.product_service.adapter.image.out.aws.config.AwsFeignConfig;
import com.vendo.product_service.adapter.image.out.aws.dto.PresignRequest;
import com.vendo.product_service.adapter.image.out.aws.dto.PresignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(
        name = "aws-service",
        path = "/internal",
        configuration = AwsFeignConfig.class)
public interface AwsClient {

    @PostMapping("/presign")
    PresignResponse presign(@RequestBody PresignRequest request);

}
