package com.stoikal.saktimart.hello;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stoikal.saktimart.common.dto.ApiResponse;

@RestController
@RequestMapping("/api/hello")
public class HelloController {

    @Value("${POSTGRES_USERNAME}")
    private String dbUser;

    @GetMapping("/plain")
    public String sayHello() {
        return "Hello, " + dbUser + "!";
    }

    @GetMapping("")
    public ApiResponse<HelloResponseDto> sayHelloJson() {
        return ApiResponse.success(new HelloResponseDto("Hello, World!"));
    }
}
