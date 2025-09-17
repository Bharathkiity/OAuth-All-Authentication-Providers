package com.oAuth.Controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
public class PublicController {
    
    @GetMapping("/home")
    public String home() {
        return "Welcome to the public home page!";
    }
}