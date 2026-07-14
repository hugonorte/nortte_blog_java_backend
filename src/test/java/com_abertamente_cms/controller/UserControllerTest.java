package com_abertamente_cms.controller;

import com_abertamente_cms.dto.user.UserResponse;
import com_abertamente_cms.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private com_abertamente_cms.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse(
                UUID.randomUUID(),
                "Hugo",
                "hugo@exemplo.com",
                null,
                "ADMIN",
                "Administrator",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @WithMockUser
    void shouldGetAllUsers() throws Exception {
        when(userService.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(userResponse)));

        mockMvc.perform(get("/api/user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Hugo"))
                .andExpect(jsonPath("$[0].email").value("hugo@exemplo.com"));
    }
}
