package com.cryptonest.auth.service;

import com.cryptonest.auth.dto.AuthResponse;
import com.cryptonest.auth.dto.LoginRequest;
import com.cryptonest.auth.dto.RegisterRequest;
import com.cryptonest.auth.entity.User;
import com.cryptonest.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Core authentication business logic.
 * This service is the only layer that combines UserRepository + JwtTokenProvider.
 * Controllers call this; they never touch repos or JWT logic directly.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    /**
     * Registers a new user account.
     * Throws if the email is already registered — never swallows duplicates silently.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                "Email already registered: " + request.getEmail()
            );
        }

        User user = User.builder()
            .fullName(request.getFullName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .build();

        user = userRepository.save(user);
        log.info("New user registered: {} (id={})", user.getEmail(), user.getId());

        String token = jwtTokenProvider.generateToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        return buildAuthResponse(user, token, refreshToken);
    }

    /**
     * Authenticates a user and returns JWT tokens.
     * Spring Security's AuthenticationManager handles the actual credential check.
     */
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails principal = (UserDetails) auth.getPrincipal();
        User user = userRepository.findByEmail(principal.getUsername())
            .orElseThrow(() -> new IllegalStateException("Authenticated user not found in DB"));

        String token = jwtTokenProvider.generateToken(user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

        log.info("User logged in: {}", user.getEmail());
        return buildAuthResponse(user, token, refreshToken);
    }

    /**
     * Issues a new access token if the provided refresh token is valid.
     */
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }
        String email = jwtTokenProvider.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalStateException("User not found for token subject"));

        String newToken = jwtTokenProvider.generateToken(email);
        String newRefresh = jwtTokenProvider.generateRefreshToken(email);
        return buildAuthResponse(user, newToken, newRefresh);
    }

    private AuthResponse buildAuthResponse(User user, String token, String refreshToken) {
        return AuthResponse.builder()
            .token(token)
            .refreshToken(refreshToken)
            .expiresIn(jwtTokenProvider.getExpirationMs())
            .userId(user.getId())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .role(user.getRole().name())
            .build();
    }
}
