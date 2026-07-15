package com_abertamente_cms.service;

import com_abertamente_cms.domain.RefreshToken;
import com_abertamente_cms.domain.User;
import com_abertamente_cms.exception.ResourceNotFoundException;
import com_abertamente_cms.repository.RefreshTokenRepository;
import com_abertamente_cms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 86400000L); // 1 day
        userId = UUID.randomUUID();
        user = new User("John", "Doe", "john@example.com", "pass");
        ReflectionTestUtils.setField(user, "id", userId);
    }

    @Test
    void shouldCreateRefreshToken() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken token = refreshTokenService.createRefreshToken(userId);

        assertNotNull(token);
        assertNotNull(token.getToken());
        assertEquals(user, token.getUser());
        verify(refreshTokenRepository).deleteByUser(user);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForCreation() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> refreshTokenService.createRefreshToken(userId));
    }

    @Test
    void shouldVerifyExpirationAndReturnToken() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusMillis(10000));

        RefreshToken verified = refreshTokenService.verifyExpiration(token);
        assertEquals(token, verified);
    }

    @Test
    void shouldThrowExceptionWhenTokenExpired() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().minusMillis(10000)); // Expired

        RuntimeException exception = assertThrows(RuntimeException.class, () -> refreshTokenService.verifyExpiration(token));
        assertEquals("Refresh token expirou. Faça login novamente.", exception.getMessage());
        verify(refreshTokenRepository).delete(token);
    }
}
