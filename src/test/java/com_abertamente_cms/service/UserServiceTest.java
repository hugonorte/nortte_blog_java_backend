package com_abertamente_cms.service;

import com_abertamente_cms.domain.User;
import com_abertamente_cms.dto.user.UserResponse;
import com_abertamente_cms.domain.User;
import com_abertamente_cms.repository.UserRepository;
import com_abertamente_cms.dto.user.UserRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;


    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User("Hugo", "Silva", "hugo@exemplo.com", "senha123");
        ReflectionTestUtils.setField(user, "id", userId);
    }

    @Test
    void shouldFindAllUsers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user));

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserResponse> result = userService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Hugo", result.getContent().get(0).firstName());
    }

    @Test
    void shouldFindById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse result = userService.findById(userId);

        assertNotNull(result);
        assertEquals("Hugo", result.firstName());
    }

    @Test
    void shouldCreateUser() {
        UserRequest request = new UserRequest("New", "User", "new@example.com", "pass123", null);
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            ReflectionTestUtils.setField(u, "id", UUID.randomUUID());
            return u;
        });

        UserResponse result = userService.create(request);

        assertNotNull(result);
        assertEquals("New", result.firstName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldUpdateUser() {
        UserRequest request = new UserRequest("Updated", "Silva", "hugo@exemplo.com", null, null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UserResponse result = userService.update(userId, request);

        assertNotNull(result);
        assertEquals("Updated", result.firstName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldDeleteUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.delete(userId);

        verify(userRepository, times(1)).delete(user);
    }
}
