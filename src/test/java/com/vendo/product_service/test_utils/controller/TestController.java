package com.vendo.product_service.test_utils.controller;

import com.vendo.product_service.test_utils.dto.PingRequest;
import com.vendo.product_service.test_utils.dto.PingResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping("/ping")
    public PingResponse ping(@RequestBody PingRequest request) {
        return new PingResponse(request.content());
    }

}
