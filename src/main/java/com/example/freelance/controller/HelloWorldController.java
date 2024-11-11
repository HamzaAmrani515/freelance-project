package com.example.freelance.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
public class HelloWorldController {
    private static final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);

    @Value("${environment}")
    private String environment;

    @GetMapping
    public String hello() {
        logger.info("Hello je suis le log spring boot.");
        return "Hello i am [ " + environment + " ] environment V20. \n";
    }
}
