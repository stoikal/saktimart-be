package com.stoikal.saktimart.hello;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
public class HelloController {

    @Value("${POSTGRES_USERNAME}")
    private String dbUser;

    @GetMapping("")
    public String sayHello() {
        return "Hello, " + dbUser + "!";
    }

    @GetMapping("/json")
    public HelloResponseDto sayHelloJson() {
        return new HelloResponseDto("Hello, World!");
    }
}
