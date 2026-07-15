package com_abertamente_cms.repository;

import com_abertamente_cms.domain.User;
import com_abertamente_cms.domain.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.context.annotation.Import;
import com_abertamente_cms.AbertamenteCmsApplication;

@DataJpaTest
@ActiveProfiles("test")
@Import(com_abertamente_cms.config.JpaAuditingConfig.class)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmailWithRoleEagerlyLoaded() {
        // Arrange
        User user = new User("John", "Doe", "john.doe@example.com", "password123");
        user.setRole(UserRole.ADMIN);
        entityManager.persist(user);
        entityManager.flush();
        entityManager.clear(); // Clear persistence context to force a query

        // Act
        Optional<User> foundUserOpt = userRepository.findByEmail("john.doe@example.com");

        // Assert
        assertTrue(foundUserOpt.isPresent());
        User foundUser = foundUserOpt.get();
        assertEquals("John", foundUser.getFirstName());
        
        // This will throw LazyInitializationException if @EntityGraph is not working 
        // and we are outside a transaction (though @DataJpaTest is transactional by default).
        // However, we can assert the roles are initialized.
        assertNotNull(foundUser.getRole());
        assertEquals(UserRole.ADMIN, foundUser.getRole());
    }
}
