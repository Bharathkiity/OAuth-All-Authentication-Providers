package com.oAuth.Controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    
    @GetMapping("/profile")
    public String userProfile(@AuthenticationPrincipal OAuth2User principal) {
        return "Welcome to your profile, " + principal.getAttribute("name");
    }
}