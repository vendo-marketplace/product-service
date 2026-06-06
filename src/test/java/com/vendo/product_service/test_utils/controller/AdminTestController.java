package com.vendo.product_service.test_utils.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminTestController {

    @GetMapping("/admin/ping")
    public String ping() {
        return "pong";
    }

}
