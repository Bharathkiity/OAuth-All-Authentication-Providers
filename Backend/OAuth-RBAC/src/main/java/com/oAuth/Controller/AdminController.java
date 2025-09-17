package com.oAuth.Controller;

import com.oAuth.Entity.Role;
import com.oAuth.Entity.User;
import com.oAuth.repo.RoleRepository;
import com.oAuth.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/promote/{email}")
    public ResponseEntity<String> promoteToAdmin(@PathVariable String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOptional.get();

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
            .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found. Initialize roles in DB."));

        user.getRoles().add(adminRole);
        userRepository.save(user);

        return ResponseEntity.ok("User promoted to ADMIN");
    }
}
