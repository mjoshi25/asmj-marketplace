package com.asmj.marketplace.auth.service;

import com.asmj.marketplace.auth.dto.AuthDtos.*;
import com.asmj.marketplace.security.JwtService;
import com.asmj.marketplace.user.model.User;
import com.asmj.marketplace.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Service @RequiredArgsConstructor
public class AuthService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthResponse register(RegisterRequest r) {
        if (r.role() == User.Role.ADMIN || r.role() == User.Role.DRIVER) throw new IllegalArgumentException("This role cannot be selected during public registration");
        if (repo.existsByEmailIgnoreCase(r.email())) throw new IllegalArgumentException("Email already registered");
        if (repo.findByMobile(r.contactNumber()).isPresent()) throw new IllegalArgumentException("Contact number already registered");
        User u = User.builder()
            .name(r.name()).email(r.email().toLowerCase().trim()).mobile(r.contactNumber().trim())
            .password(encoder.encode(r.password())).roles(Set.of(r.role()))
            .status(User.Status.ACTIVE).createdAt(Instant.now()).updatedAt(Instant.now()).build();
        repo.save(u);
        return response(u);
    }

    public AuthResponse login(LoginRequest r) {
        User u = repo.findByEmailIgnoreCase(r.email()).orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!encoder.matches(r.password(), u.getPassword())) throw new IllegalArgumentException("Invalid credentials");
        if (u.getStatus() != User.Status.ACTIVE) throw new IllegalArgumentException("Account is not active");
        return response(u);
    }

    public User current(String email) { return repo.findByEmailIgnoreCase(email).orElseThrow(); }

    private AuthResponse response(User u) {
        String token = jwt.generate(u.getEmail(), u.getRoles().stream().map(Enum::name).toList());
        return new AuthResponse(token, token, u.getId(), u.getName(), u.getEmail(), u.getMobile(), u.getRoles());
    }
}
