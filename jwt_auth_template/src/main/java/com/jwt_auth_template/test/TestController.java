package com.jwt_auth_template.test;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String index() {
        return "Hello World";
    }

    @GetMapping("/auth/me")
    public String me() {
        return "Hello Me";
    }
}
