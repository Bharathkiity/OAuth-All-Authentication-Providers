package com.oAuth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.oAuth.repo")  // Your repository package
@EntityScan("com.oAuth.Entity")           // Your entity package
public class OAuthRbacApplication {
    public static void main(String[] args) {
        SpringApplication.run(OAuthRbacApplication.class, args);
    }
}