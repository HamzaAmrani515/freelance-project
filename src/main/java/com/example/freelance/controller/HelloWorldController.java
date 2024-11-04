package com.example.freelance.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
public class HelloWorldController {
    @GetMapping
    public String hello() {
        return "hello i am Assala Hamoudi V2. \n";
    }
}
