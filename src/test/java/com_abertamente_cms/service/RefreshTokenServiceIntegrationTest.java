package com_abertamente_cms.service;

import com_abertamente_cms.domain.RefreshToken;
import com_abertamente_cms.domain.User;
import com_abertamente_cms.repository.RefreshTokenRepository;
import com_abertamente_cms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional // Garante que as mudanças no banco sofram rollback após o teste
class RefreshTokenServiceIntegrationTest {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @BeforeEach
    void setUp() {
        // Preparar um usuário real no banco de dados em memória (H2/Testcontainers)
        User user = new User("Integration User", "integration@example.com", "password");
        savedUser = userRepository.saveAndFlush(user);
    }

    @Test
    void shouldCreateRefreshTokenTwiceWithoutConstraintViolation() {
        // Ação 1: Criar o primeiro token
        RefreshToken firstToken = refreshTokenService.createRefreshToken(savedUser.getId());
        
        assertNotNull(firstToken);
        assertNotNull(firstToken.getId());
        
        // Verifica se o token foi realmente salvo
        Optional<RefreshToken> retrievedFirst = refreshTokenRepository.findByUser(savedUser);
        assertTrue(retrievedFirst.isPresent());
        assertEquals(firstToken.getToken(), retrievedFirst.get().getToken());

        // Ação 2: Criar o SEGUNDO token para o mesmo usuário
        // Se a ordem de delete/insert do Hibernate estiver errada (sem o @Modifying), 
        // isso lançará uma DataIntegrityViolationException devido à unique constraint.
        assertDoesNotThrow(() -> {
            RefreshToken secondToken = refreshTokenService.createRefreshToken(savedUser.getId());
            
            // Verifica se o segundo token sobrescreveu o primeiro corretamente
            Optional<RefreshToken> retrievedSecond = refreshTokenRepository.findByUser(savedUser);
            assertTrue(retrievedSecond.isPresent());
            assertEquals(secondToken.getToken(), retrievedSecond.get().getToken());
            assertNotEquals(firstToken.getToken(), secondToken.getToken());
        });
    }
}
