package com_abertamente_cms.service;

import com_abertamente_cms.domain.RefreshToken;
import com_abertamente_cms.domain.User;
import com_abertamente_cms.dto.auth.AuthResponse;
import com_abertamente_cms.dto.auth.LoginRequest;
import com_abertamente_cms.dto.auth.RegisterRequest;
import com_abertamente_cms.dto.auth.TokenRefreshRequest;
import com_abertamente_cms.dto.auth.TokenRefreshResponse;
import com_abertamente_cms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        user = new User("Jane", "Doe", "jane@example.com", "encoded_pass");
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());

        refreshToken = new RefreshToken();
        refreshToken.setToken("refresh_token_string");
        refreshToken.setUser(user);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest("Jane", "Doe", "jane@example.com", "password123");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password())).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt_token");
        when(refreshTokenService.createRefreshToken(user.getId())).thenReturn(refreshToken);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("jwt_token", response.token());
        assertEquals("refresh_token_string", response.refreshToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenRegisteringExistingEmail() {
        RegisterRequest request = new RegisterRequest("Jane", "Doe", "jane@example.com", "password123");
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        assertEquals("E-mail já está em uso.", exception.getMessage());
    }

    @Test
    void shouldLoginUserSuccessfully() {
        LoginRequest request = new LoginRequest("jane@example.com", "password123");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt_token");
        when(refreshTokenService.createRefreshToken(user.getId())).thenReturn(refreshToken);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt_token", response.token());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void shouldRefreshAccessToken() {
        TokenRefreshRequest request = new TokenRefreshRequest("valid_refresh_token");

        when(refreshTokenService.findByToken(request.refreshToken())).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
        when(jwtService.generateToken(user)).thenReturn("new_jwt_token");

        TokenRefreshResponse response = authService.refreshToken(request);

        assertNotNull(response);
        assertEquals("new_jwt_token", response.accessToken());
        assertEquals("valid_refresh_token", response.refreshToken());
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenNotFound() {
        TokenRefreshRequest request = new TokenRefreshRequest("invalid_refresh_token");

        when(refreshTokenService.findByToken(request.refreshToken())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.refreshToken(request));
        assertEquals("Refresh token não está no banco de dados!", exception.getMessage());
    }
}
