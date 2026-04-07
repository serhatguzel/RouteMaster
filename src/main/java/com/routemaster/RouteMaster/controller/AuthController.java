package com.routemaster.RouteMaster.controller;

import com.routemaster.RouteMaster.dto.AuthRequestDto;
import com.routemaster.RouteMaster.dto.AuthResponseDto;
import com.routemaster.RouteMaster.entity.User;
import com.routemaster.RouteMaster.repository.UserRepository;
import com.routemaster.RouteMaster.security.JwtUtil;
import com.routemaster.RouteMaster.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsService userDetailsService, RefreshTokenService refreshTokenService, UserRepository userRepository,
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthRequestDto request) {

        log.info("Login Trial -> User: {}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        String refreshToken = refreshTokenService.createRefreshToken(user.getId());

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority())
                .orElse("USER");

        log.info("Login is successful: User: {} is in the system", request.getUsername());

        return ResponseEntity.ok(new AuthResponseDto(jwt,refreshToken, role));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody String refreshToken) {
        // Redis'ten userId'yi çekelim:
        String userIdStr = refreshTokenService.getUserIdFromRefreshToken(refreshToken);

        if (userIdStr == null) {
            log.warn("Invalid or expired Refresh Token!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token is expired!");
        }

        User user = userRepository.findById(Long.valueOf(userIdStr))
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtUtil.generateToken(user.getUsername());

        log.info("Access Token refreshed for UserID: {}", userIdStr);

        return ResponseEntity.ok(new AuthResponseDto(newAccessToken, refreshToken, ""));
    }
}
