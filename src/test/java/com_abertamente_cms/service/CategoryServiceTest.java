package com_abertamente_cms.service;

import com_abertamente_cms.domain.Category;
import com_abertamente_cms.dto.category.CategoryRequest;
import com_abertamente_cms.dto.category.CategoryResponse;
import com_abertamente_cms.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        category = new Category("Tecnologia", "tecnologia", "Posts tech");
        ReflectionTestUtils.setField(category, "id", categoryId);
    }

    @Test
    void shouldCreateCategory() {
        CategoryRequest request = new CategoryRequest("Tecnologia", "tecnologia", "Posts tech");
        when(categoryRepository.findBySlug(request.slug())).thenReturn(Optional.empty());
        when(categoryRepository.findByName(request.name())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse response = categoryService.create(request);

        assertNotNull(response);
        assertEquals("Tecnologia", response.name());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void shouldThrowExceptionWhenSlugAlreadyExistsOnCreate() {
        CategoryRequest request = new CategoryRequest("Outro", "tecnologia", "Posts tech");
        when(categoryRepository.findBySlug(request.slug())).thenReturn(Optional.of(category));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> categoryService.create(request));
        assertEquals("Categoria com mesmo slug já existe.", exception.getMessage());
    }

    @Test
    void shouldUpdateCategory() {
        CategoryRequest request = new CategoryRequest("Novo Nome", "novo-slug", "Novo");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.findByName(request.name())).thenReturn(Optional.empty());
        when(categoryRepository.findBySlug(request.slug())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

        CategoryResponse response = categoryService.update(categoryId, request);

        assertEquals("Novo Nome", response.name());
        assertEquals("novo-slug", response.slug());
    }

    @Test
    void shouldDeleteCategory() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        categoryService.delete(categoryId);

        verify(categoryRepository).delete(category);
    }
}
