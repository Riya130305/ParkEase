package com.parking.service;

import com.parking.dto.AuthResponse;
import com.parking.dto.LoginRequest;
import com.parking.dto.RegisterRequest;
import com.parking.entity.User;
import com.parking.entity.UserRole;
import com.parking.exception.ConflictException;
import com.parking.repository.UserRepository;
import com.parking.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered");
        }

        User user = userRepository.save(User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build());

        return response(user);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            ));
        } catch (AuthenticationException ex) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        return response(user);
    }

    private AuthResponse response(User user) {
        UserRole role = user.getRole() == null ? UserRole.USER : user.getRole();
        return new AuthResponse(
                jwtService.generateToken(user.getEmail()),
                user.getId(),
                user.getName(),
                user.getEmail(),
                role.name()
        );
    }
}
