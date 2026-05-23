package com.stoikal.saktimart.hello;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stoikal.saktimart.common.dto.ApiEnvelope;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Hello", description = "Just say hello")
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
    public ApiEnvelope<HelloResponseDto> sayHelloJson() {
        return ApiEnvelope.success(new HelloResponseDto("Hello, World!"));
    }
}
