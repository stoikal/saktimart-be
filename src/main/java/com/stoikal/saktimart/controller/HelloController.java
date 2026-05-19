package com.stoikal.saktimart.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stoikal.saktimart.dto.HelloResponse;

@RestController
@RequestMapping("/hello")
public class HelloController {
    
    @GetMapping("")
    public String sayHello() {
        return "Hello, World!";
    }

    @GetMapping("/json")
    public HelloResponse sayHelloJson() {
        return new HelloResponse("Hello, World!");
    }
}
