package com_abertamente_cms.controller;

import com_abertamente_cms.dto.user.UserResponse;
import com_abertamente_cms.dto.user.UserRequest;
import com_abertamente_cms.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                "Silva",
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
                .andExpect(jsonPath("$.content[0].first_name").value("Hugo"))
                .andExpect(jsonPath("$.content[0].email").value("hugo@exemplo.com"));
    }

    @Test
    @WithMockUser
    void shouldGetUserById() throws Exception {
        when(userService.findById(userResponse.id())).thenReturn(userResponse);

        mockMvc.perform(get("/api/user/" + userResponse.id())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value("Hugo"));
    }

    @Test
    @WithMockUser
    void shouldCreateUser() throws Exception {
        UserRequest request = new UserRequest("Hugo", "Silva", "hugo@exemplo.com", "senha123", null);
        when(userService.create(any(UserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/user")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.first_name").value("Hugo"));
    }

    @Test
    @WithMockUser
    void shouldUpdateUser() throws Exception {
        UserRequest request = new UserRequest("Hugo Edit", "Silva", "hugo.edit@exemplo.com", null, null);
        UserResponse updatedResponse = new UserResponse(
                userResponse.id(), "Hugo Edit", "Silva", "hugo.edit@exemplo.com", null, "ADMIN", "Administrator", LocalDateTime.now(), LocalDateTime.now()
        );
        when(userService.update(eq(userResponse.id()), any(UserRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/user/" + userResponse.id())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value("Hugo Edit"));
    }

    @Test
    @WithMockUser
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).delete(userResponse.id());

        mockMvc.perform(delete("/api/user/" + userResponse.id())
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
