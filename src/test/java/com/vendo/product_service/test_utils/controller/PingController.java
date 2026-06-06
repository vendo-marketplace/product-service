package com.vendo.product_service.test_utils.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class PingController {

    @GetMapping("/ping")
    public String getPing() {
        return "pong";
    }

    @PostMapping("/ping")
    public String postPing(@RequestBody RequestPing request) {
        return request.value();
    }

    @GetMapping("/internal/ping")
    public String internalPing() {
        return "pong";
    }

    @GetMapping("/admin/ping")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String adminPing() {
        return "pong";
    }

}
