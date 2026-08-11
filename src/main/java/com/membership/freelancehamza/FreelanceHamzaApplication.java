package com.membership.freelancehamza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.membership.freelancehamza")
@EnableJpaRepositories(basePackages = "com.membership.freelancehamza.repository")
public class FreelanceHamzaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreelanceHamzaApplication.class, args);
    }
}