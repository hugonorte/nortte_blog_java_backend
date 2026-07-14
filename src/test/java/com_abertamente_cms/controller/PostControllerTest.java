package com_abertamente_cms.controller;

import com_abertamente_cms.domain.PostStatus;
import com_abertamente_cms.dto.category.CategoryResponse;
import com_abertamente_cms.dto.post.PostRequest;
import com_abertamente_cms.dto.post.PostResponse;
import com_abertamente_cms.security.JwtAuthenticationFilter;
import com_abertamente_cms.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private PostResponse buildResponse() {
        CategoryResponse categoryResponse = new CategoryResponse(UUID.randomUUID(), "Tech", "tech", "desc", LocalDateTime.now(), null);
        return new PostResponse(
                UUID.randomUUID(),
                "Title",
                "title",
                "Content",
                "tldr",
                "/uploads/posts/cover.jpg",
                PostStatus.DRAFT,
                UUID.randomUUID(),
                "Author",
                categoryResponse,
                java.time.Instant.now(),
                LocalDateTime.now(),
                null
        );
    }

    @Test
    void testIndex() throws Exception {
        PostResponse response = buildResponse();
        Page<PostResponse> page = new PageImpl<>(Collections.singletonList(response), PageRequest.of(0, 10), 1);

        when(postService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/post"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Title"));
    }

    @Test
    void testShow() throws Exception {
        PostResponse response = buildResponse();
        UUID postId = response.id();

        when(postService.findById(postId)).thenReturn(response);

        mockMvc.perform(get("/api/post/" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void testStore() throws Exception {
        PostRequest request = new PostRequest("Title", "title", "Content", "tldr", "/uploads/posts/cover.jpg", java.time.Instant.now(), UUID.randomUUID(), UUID.randomUUID());
        PostResponse response = buildResponse();

        when(postService.create(any(PostRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void testUpdate() throws Exception {
        UUID postId = UUID.randomUUID();
        PostRequest request = new PostRequest("Title Edit", "title-edit", "Content", "tldr edit", "/uploads/posts/cover.jpg", java.time.Instant.now(), UUID.randomUUID(), UUID.randomUUID());
        
        CategoryResponse categoryResponse = new CategoryResponse(UUID.randomUUID(), "Tech", "tech", "desc", LocalDateTime.now(), null);
        PostResponse response = new PostResponse(postId, "Title Edit", "title-edit", "Content", "tldr edit", "/uploads/posts/cover.jpg", PostStatus.DRAFT, UUID.randomUUID(), "Author", categoryResponse, java.time.Instant.now(), LocalDateTime.now(), null);

        when(postService.update(eq(postId), any(PostRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/post/" + postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title Edit"));
    }

    @Test
    void testDestroy() throws Exception {
        UUID postId = UUID.randomUUID();
        doNothing().when(postService).delete(postId);

        mockMvc.perform(delete("/api/post/" + postId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testChangeStatus() throws Exception {
        UUID postId = UUID.randomUUID();
        CategoryResponse categoryResponse = new CategoryResponse(UUID.randomUUID(), "Tech", "tech", "desc", LocalDateTime.now(), null);
        PostResponse response = new PostResponse(postId, "Title", "title", "Content", "tldr", "/uploads/posts/cover.jpg", PostStatus.PUBLISHED, UUID.randomUUID(), "Author", categoryResponse, java.time.Instant.now(), LocalDateTime.now(), null);

        when(postService.changeStatus(postId, PostStatus.PUBLISHED)).thenReturn(response);

        mockMvc.perform(patch("/api/post/" + postId + "/status")
                        .param("status", "PUBLISHED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }
}
