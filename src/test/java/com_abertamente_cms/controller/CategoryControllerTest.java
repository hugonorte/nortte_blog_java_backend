package com_abertamente_cms.controller;

import com_abertamente_cms.dto.category.CategoryRequest;
import com_abertamente_cms.dto.category.CategoryResponse;
import com_abertamente_cms.security.JwtAuthenticationFilter;
import com_abertamente_cms.service.CategoryService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void testIndex() throws Exception {
        UUID categoryId = UUID.randomUUID();
        CategoryResponse response = new CategoryResponse(categoryId, "Tech", "tech", "desc", LocalDateTime.now(), null);
        Page<CategoryResponse> page = new PageImpl<>(Collections.singletonList(response), PageRequest.of(0, 10), 1);

        when(categoryService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/category"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Tech"));
    }

    @Test
    void testShow() throws Exception {
        UUID categoryId = UUID.randomUUID();
        CategoryResponse response = new CategoryResponse(categoryId, "Tech", "tech", "desc", LocalDateTime.now(), null);

        when(categoryService.findById(categoryId)).thenReturn(response);

        mockMvc.perform(get("/api/category/" + categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tech"));
    }

    @Test
    void testStore() throws Exception {
        CategoryRequest request = new CategoryRequest("Tech", "tech", "desc");
        UUID categoryId = UUID.randomUUID();
        CategoryResponse response = new CategoryResponse(categoryId, "Tech", "tech", "desc", LocalDateTime.now(), null);

        when(categoryService.create(any(CategoryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Tech"));
    }

    @Test
    void testUpdate() throws Exception {
        UUID categoryId = UUID.randomUUID();
        CategoryRequest request = new CategoryRequest("Tech Edit", "tech-edit", "desc");
        CategoryResponse response = new CategoryResponse(categoryId, "Tech Edit", "tech-edit", "desc", LocalDateTime.now(), null);

        when(categoryService.update(eq(categoryId), any(CategoryRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/category/" + categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tech Edit"));
    }

    @Test
    void testDestroy() throws Exception {
        UUID categoryId = UUID.randomUUID();
        doNothing().when(categoryService).delete(categoryId);

        mockMvc.perform(delete("/api/category/" + categoryId))
                .andExpect(status().isNoContent());
    }
}
