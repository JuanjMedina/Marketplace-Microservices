package com.marketplace.userservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/test-1")
    @PreAuthorize("hasRole('admin_client_role')")
    public String test() {
        return "Test endpoint is working! - ADMIN";
    }

    @GetMapping("/test-2")
    @PreAuthorize("hasRole('buyer_client_role') or hasRole('admin_client_role')")
    public String test2() {
        return "Test endpoint is working! - BUYER";
    }
}
