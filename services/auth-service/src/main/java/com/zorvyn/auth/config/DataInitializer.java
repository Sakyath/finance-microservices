package com.zorvyn.auth.config;

import com.zorvyn.auth.entity.AuthUser;
import com.zorvyn.auth.repository.AuthUserRepository;
import com.zorvyn.common.model.enums.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AuthUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AuthUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!repository.existsByUsername("admin")) {
            AuthUser admin = AuthUser.builder()
                    .username("admin")
                    .email("admin@zorvyn.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .active(true)
                    .build();
            repository.save(admin);
            System.out.println("Default admin created: admin / admin123");
        }
    }
}
