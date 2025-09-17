package com.oAuth.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oAuth.dto.UserDto;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/user")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return ResponseEntity.ok(new UserDto());
        }
        
        UserDto userDto = new UserDto();
        userDto.setName(principal.getAttribute("name"));
        userDto.setEmail(principal.getAttribute("email"));
        userDto.setRole(principal.getAuthorities().stream()
            .findFirst()
            .map(grantedAuthority -> grantedAuthority.getAuthority())
            .orElse("ROLE_USER"));
        
        return ResponseEntity.ok(userDto);
    }
}