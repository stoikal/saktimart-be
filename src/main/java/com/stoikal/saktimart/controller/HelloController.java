package com.stoikal.saktimart.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stoikal.saktimart.dto.HelloResponse;

@RestController
@RequestMapping("/hello")
public class HelloController {

    @Value("${POSTGRES_USERNAME}")
    private String dbUser;

    @GetMapping("")
    public String sayHello() {
        return "Hello, " + dbUser + "!";
    }

    @GetMapping("/json")
    public HelloResponse sayHelloJson() {
        return new HelloResponse("Hello, World!");
    }
}
