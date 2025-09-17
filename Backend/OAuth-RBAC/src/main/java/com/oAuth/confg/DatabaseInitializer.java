package com.oAuth.confg;

import com.oAuth.Entity.Role;
import com.oAuth.repo.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    private final RoleRepository roleRepository;

    public DatabaseInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void init() {
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            Role user = new Role();
            user.setName("ROLE_USER");
            roleRepository.save(user);
        }

        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            Role admin = new Role();
            admin.setName("ROLE_ADMIN");
            roleRepository.save(admin);
        }
    }
}
