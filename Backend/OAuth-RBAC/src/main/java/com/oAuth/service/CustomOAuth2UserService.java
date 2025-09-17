package com.oAuth.service;

import com.oAuth.Entity.Role;
import com.oAuth.Entity.User;
import com.oAuth.repo.RoleRepository;
import com.oAuth.repo.UserRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private static final Set<String> ADMIN_EMAILS = Set.of("admin@gmail.com", "bharathkitty07@gmail.com", "bharathkitty9009@gmail.com");

    public CustomOAuth2UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // github, google, etc.
        String email = null;
        String name = null;

        if ("github".equals(registrationId)) {
            email = oauth2User.getAttribute("email"); // often null
            name = oauth2User.getAttribute("login");

            // if email is null, fetch /user/emails manually
            if (email == null) {
                email = fetchPrimaryGitHubEmail(userRequest);
            }

            if (email == null) {
                throw new OAuth2AuthenticationException("GitHub email not found or private.");
            }

        } else {
            email = oauth2User.getAttribute("email");
            name = oauth2User.getAttribute("name");
        }

        if (email == null) throw new OAuth2AuthenticationException("No email found from provider");

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = createNewUser(email, name);
        } else {
            updateUserRoles(user, email);
        }

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toSet());

        System.out.println("✅ GitHub Login Success:");
        System.out.println("👤 Name: " + name);
        System.out.println("📧 Email: " + email);
        System.out.println("🪪 Login ID: " + oauth2User.getAttribute("login"));

        // Fix: create a new attributes map overriding "email" to ensure it's not null
        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
        attributes.put("email", email);

        return new DefaultOAuth2User(authorities, attributes, "email");
    }

    private String fetchPrimaryGitHubEmail(OAuth2UserRequest userRequest) {
        String token = userRequest.getAccessToken().getTokenValue();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                "https://api.github.com/user/emails",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        return response.getBody().stream()
                .filter(email -> Boolean.TRUE.equals(email.get("primary")))
                .map(email -> (String) email.get("email"))
                .findFirst()
                .orElse(null);
    }

    private User createNewUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);

        Set<Role> roles = new HashSet<>();

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
        roles.add(userRole);

        if (ADMIN_EMAILS.contains(email)) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));
            roles.add(adminRole);
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }

    private void updateUserRoles(User user, String email) {
        Set<Role> newRoles = new HashSet<>();

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
        newRoles.add(userRole);

        if (ADMIN_EMAILS.contains(email)) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));
            newRoles.add(adminRole);
        }

        user.setRoles(newRoles);
        userRepository.save(user);
    }
}
