package com.zorvyn.auth.service;

import com.zorvyn.auth.dto.AuthResponse;
import com.zorvyn.auth.dto.LoginRequest;
import com.zorvyn.auth.dto.RegisterRequest;
import com.zorvyn.auth.entity.AuthUser;
import com.zorvyn.auth.repository.AuthUserRepository;
import com.zorvyn.common.exception.BadRequestException;
import com.zorvyn.common.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final AuthUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(AuthUserRepository repository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (repository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken: " + request.getUsername());
        }
        if (repository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        AuthUser user = AuthUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .build();

        AuthUser saved = repository.save(user);

        Map<String, Object> claims = Map.of(
                "role", "ROLE_" + saved.getRole().name(),
                "userId", saved.getId()
        );
        String token = jwtTokenProvider.generateToken(saved, claims);
        return new AuthResponse(token, saved.getUsername(), saved.getRole().name(), saved.getId());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        AuthUser user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));

        Map<String, Object> claims = Map.of(
                "role", "ROLE_" + user.getRole().name(),
                "userId", user.getId()
        );
        String token = jwtTokenProvider.generateToken(user, claims);
        return new AuthResponse(token, user.getUsername(), user.getRole().name(), user.getId());
    }
}
