package com_abertamente_cms.controller;

import com_abertamente_cms.dto.auth.AuthResponse;
import com_abertamente_cms.dto.auth.LoginRequest;
import com_abertamente_cms.dto.auth.RegisterRequest;
import com_abertamente_cms.security.JwtAuthenticationFilter;
import com_abertamente_cms.service.AuthService;
import com_abertamente_cms.service.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Desativa os filtros de segurança para focar no Controller
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private RefreshTokenService refreshTokenService;
    
    // Precisamos mockar os filtros customizados que possam ser carregados pelo SecurityConfig
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldRegisterUser() throws Exception {
        RegisterRequest request = new RegisterRequest("Jane", "Doe", "jane@example.com", "password123");
        AuthResponse response = new AuthResponse("jwt_token", "refresh_token", UUID.randomUUID(), "Jane", "Doe", "jane@example.com", "USER");

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt_token"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie().exists("refreshToken"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie().httpOnly("refreshToken", true));
    }

    @Test
    void shouldFailRegisterWhenEmailIsInvalid() throws Exception {
        RegisterRequest request = new RegisterRequest("Jane", "Doe", "invalid-email", "password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("https://abertamente.net/erros/validacao-falhou"))
                .andExpect(jsonPath("$.title").value("Erro de Validação"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void shouldLoginUser() throws Exception {
        LoginRequest request = new LoginRequest("jane@example.com", "password123");
        AuthResponse response = new AuthResponse("jwt_token", "refresh_token", UUID.randomUUID(), "Jane", "Doe", "jane@example.com", "USER");

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt_token"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie().exists("refreshToken"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie().httpOnly("refreshToken", true));
    }

    @Test
    void shouldRefreshToken() throws Exception {
        com_abertamente_cms.dto.auth.TokenRefreshResponse response = new com_abertamente_cms.dto.auth.TokenRefreshResponse("new_jwt_token", "new_refresh_token", "Bearer");
        when(authService.refreshToken(any(com_abertamente_cms.dto.auth.TokenRefreshRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/refresh")
                .cookie(new jakarta.servlet.http.Cookie("refreshToken", "old_refresh_token")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new_jwt_token"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie().exists("refreshToken"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie().value("refreshToken", "new_refresh_token"));
    }

    @Test
    void shouldFailRefreshWithoutCookie() throws Exception {
        mockMvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldLogout() throws Exception {
        com_abertamente_cms.domain.RefreshToken token = new com_abertamente_cms.domain.RefreshToken();
        com_abertamente_cms.domain.User user = new com_abertamente_cms.domain.User("Test", "User", "test@example.com", "pass");
        user.setId(UUID.randomUUID());
        token.setUser(user);
        token.setToken("old_refresh_token");

        when(refreshTokenService.findByToken("old_refresh_token")).thenReturn(java.util.Optional.of(token));

        mockMvc.perform(post("/api/auth/logout")
                .cookie(new jakarta.servlet.http.Cookie("refreshToken", "old_refresh_token")))
                .andExpect(status().isNoContent())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie().exists("refreshToken"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie().maxAge("refreshToken", 0));
    }

    @Test
    void shouldReturnMe() throws Exception {
        com_abertamente_cms.dto.user.UserDto userDto = new com_abertamente_cms.dto.user.UserDto(UUID.randomUUID(), "John", "Doe", "john@example.com", "USER");
        when(authService.getMe()).thenReturn(userDto);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name").value("John"));
    }
}
