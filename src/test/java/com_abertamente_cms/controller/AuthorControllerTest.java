package com_abertamente_cms.controller;

import com_abertamente_cms.domain.User;
import com_abertamente_cms.dto.author.AuthorRequest;
import com_abertamente_cms.dto.author.AuthorResponse;
import com_abertamente_cms.service.AuthorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private com_abertamente_cms.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthorResponse response;
    private UUID authorId;

    @BeforeEach
    void setUp() {
        authorId = UUID.randomUUID();
        response = new AuthorResponse(authorId, "Jane Doe", "jane.doe@example.com", "Bio", "Title", "Twitter", "handle");
    }

    @Test
    void shouldCreateAuthor() throws Exception {
        AuthorRequest request = new AuthorRequest("Jane Doe", "jane.doe@example.com", "Bio", "Title", "Twitter", "handle");

        when(authorService.create(any(AuthorRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/author")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(authorId.toString()))
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane.doe@example.com"))
                .andExpect(jsonPath("$.bio").value("Bio"))
                .andExpect(jsonPath("$.main_title").value("Title"));
    }

    @Test
    void shouldReturnBadRequestWhenCreatingAuthorWithInvalidData() throws Exception {
        AuthorRequest request = new AuthorRequest("", "invalid-email", "", "", "Twitter", "handle");

        mockMvc.perform(post("/api/author")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalid_params.name").exists())
                .andExpect(jsonPath("$.invalid_params.email").exists())
                .andExpect(jsonPath("$.invalid_params.bio").exists())
                .andExpect(jsonPath("$.invalid_params.mainTitle").exists());
    }

    @Test
    void shouldGetAllAuthors() throws Exception {
        when(authorService.getAllAuthors()).thenReturn(java.util.List.of(response));

        mockMvc.perform(get("/api/author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(authorId.toString()));
    }

    @Test
    void shouldGetAuthorById() throws Exception {
        when(authorService.getAuthorById(authorId)).thenReturn(response);

        mockMvc.perform(get("/api/author/" + authorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(authorId.toString()));
    }

    @Test
    void shouldReturnNotFoundWhenAuthorDoesNotExist() throws Exception {
        when(authorService.getAuthorById(any(UUID.class))).thenReturn(null);

        mockMvc.perform(get("/api/author/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}
