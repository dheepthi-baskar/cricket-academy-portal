package com.cricketacademy.service;

import com.cricketacademy.domain.AcademyUser;
import com.cricketacademy.domain.Batch;
import com.cricketacademy.domain.Player;
import com.cricketacademy.domain.Role;
import com.cricketacademy.repo.AcademyUserRepository;
import com.cricketacademy.repo.BatchRepository;
import com.cricketacademy.repo.PlayerRepository;
import com.cricketacademy.security.AcademyUserDetails;
import com.cricketacademy.security.JwtService;
import com.cricketacademy.web.dto.LoginRequest;
import com.cricketacademy.web.dto.RegisterRequest;
import com.cricketacademy.web.dto.TokenResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AcademyUserRepository users;
    private final BatchRepository batches;
    private final PlayerRepository players;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AuthenticationManager authenticationManager,
            AcademyUserRepository users,
            BatchRepository batches,
            PlayerRepository players,
            JwtService jwtService,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.users = users;
        this.batches = batches;
        this.players = players;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public TokenResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        AcademyUserDetails principal =
                (AcademyUserDetails) users.findByEmailIgnoreCase(request.email())
                        .map(u -> new AcademyUserDetails(
                                u.getId(), u.getEmail(), u.getPasswordHash(), u.getRole(), u.isEnabled()))
                        .orElseThrow();
        String token = jwtService.generateToken(principal);
        return new TokenResponse(token, principal.getId(), principal.getRole().name());
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (users.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (request.role() == Role.PLAYER && request.batchId() == null) {
            throw new IllegalArgumentException("Players must be assigned to a batch");
        }
        AcademyUser user = AcademyUser.builder()
                .email(request.email().trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role(request.role())
                .enabled(true)
                .build();
        user = users.save(user);

        if (request.role() == Role.PLAYER) {
            Batch batch = batches.findById(request.batchId()).orElseThrow();
            Player player = Player.builder()
                    .user(user)
                    .batch(batch)
                    .displayName(request.fullName())
                    .build();
            players.save(player);
        }

        AcademyUserDetails principal = new AcademyUserDetails(
                user.getId(), user.getEmail(), user.getPasswordHash(), user.getRole(), user.isEnabled());
        return new TokenResponse(jwtService.generateToken(principal), user.getId(), user.getRole().name());
    }
}
