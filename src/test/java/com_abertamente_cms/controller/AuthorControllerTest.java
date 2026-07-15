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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
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
        Page<AuthorResponse> page = new PageImpl<>(Collections.singletonList(response), PageRequest.of(0, 10), 1);
        when(authorService.getAllAuthors(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(authorId.toString()));
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

    @Test
    void shouldUpdateAuthor() throws Exception {
        AuthorRequest request = new AuthorRequest("Jane Edit", "jane.edit@example.com", "Bio", "Title", "Twitter", "handle");
        AuthorResponse updatedResponse = new AuthorResponse(authorId, "Jane Edit", "jane.edit@example.com", "Bio", "Title", "Twitter", "handle");

        when(authorService.update(eq(authorId), any(AuthorRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/author/" + authorId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Edit"));
    }

    @Test
    void shouldDeleteAuthor() throws Exception {
        doNothing().when(authorService).delete(authorId);

        mockMvc.perform(delete("/api/author/" + authorId)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
