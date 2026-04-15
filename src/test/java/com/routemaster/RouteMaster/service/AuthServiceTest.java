package com.routemaster.RouteMaster.service;

import com.routemaster.RouteMaster.dto.AuthRequestDto;
import com.routemaster.RouteMaster.dto.AuthResponseDto;
import com.routemaster.RouteMaster.entity.User;
import com.routemaster.RouteMaster.repository.UserRepository;
import com.routemaster.RouteMaster.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private AuthRequestDto authRequest;
    private User testUser;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequestDto("testUser", "paasword123");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");

        userDetails = mock(UserDetails.class);
    }

    @Test
    @DisplayName("Should login successfully and return token set")
    void shouldLoginSuccessfully() {
        // GIVEN
        when(userRepository.findWithRolesByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .when(userDetails).getAuthorities();
        
        when(jwtUtil.generateToken(anyString())).thenReturn("mock-jwt-token");
        when(refreshTokenService.createRefreshToken(anyLong())).thenReturn("mock-refresh-token");

        // WHEN
        AuthResponseDto response = authService.login(authRequest);

        // THEN
        assertNotNull(response);
        assertEquals("mock-jwt-token", response.accessToken());
        assertEquals("mock-refresh-token", response.refreshToken());
        assertEquals("ROLE_USER", response.role());
        
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Should throw exception during login when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // GIVEN
        when(userRepository.findWithRolesByUsername(anyString())).thenReturn(Optional.empty());

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(authRequest);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void shouldRefreshTokenSuccessfully() {
        // GIVEN
        String oldRefreshToken = "old-refresh-token";
        when(refreshTokenService.getUserIdFromRefreshToken(anyString())).thenReturn("1");
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(jwtUtil.generateToken(anyString())).thenReturn("new-access-token");

        // WHEN
        AuthResponseDto response = authService.refreshToken(oldRefreshToken);

        // THEN
        assertNotNull(response);
        assertEquals("new-access-token", response.accessToken());
        assertEquals(oldRefreshToken, response.refreshToken());
        verify(jwtUtil).generateToken("testuser");
    }

    @Test
    @DisplayName("Should throw exception when refresh token is invalid or expired")
    void shouldThrowExceptionWhenRefreshTokenInvalid() {
        // GIVEN
        when(refreshTokenService.getUserIdFromRefreshToken(anyString())).thenReturn(null);

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.refreshToken("invalid-token");
        });

        assertTrue(exception.getMessage().contains("Refresh Token is expired"));
    }
}
