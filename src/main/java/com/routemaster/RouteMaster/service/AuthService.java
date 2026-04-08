package com.routemaster.RouteMaster.service;

import com.routemaster.RouteMaster.dto.AuthRequestDto;
import com.routemaster.RouteMaster.dto.AuthResponseDto;
import com.routemaster.RouteMaster.entity.User;
import com.routemaster.RouteMaster.repository.UserRepository;
import com.routemaster.RouteMaster.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthResponseDto login(AuthRequestDto request) {
        log.info("Login Attempt -> User: {}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());
        String refreshToken = refreshTokenService.createRefreshToken(user.getId());

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority())
                .orElse("USER");

        log.info("Login successful: User: {} is in the system", request.getUsername());

        return new AuthResponseDto(jwt, refreshToken, role);
    }

    public AuthResponseDto refreshToken(String refreshToken) {
        String userIdStr = refreshTokenService.getUserIdFromRefreshToken(refreshToken);

        if (userIdStr == null) {
            log.warn("Invalid or expired Refresh Token!");
            throw new RuntimeException("Refresh Token is expired or invalid!");
        }

        User user = userRepository.findById(Long.valueOf(userIdStr))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String newAccessToken = jwtUtil.generateToken(user.getUsername());

        log.info("Access Token refreshed for UserID: {}", userIdStr);

        return new AuthResponseDto(newAccessToken, refreshToken, "");
    }
}
