package com.oAuth.service;

import com.oAuth.util.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;

    public CustomOAuth2SuccessHandler(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String email = authentication.getName();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String token = jwtTokenUtil.generateToken(email, roles);

        // Frontend receives JWT token via redirect
        response.sendRedirect("http://localhost:4200/login/success?token=" + token);
    }
}
