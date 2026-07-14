package com_abertamente_cms.service;

import com_abertamente_cms.domain.Author;
import com_abertamente_cms.domain.User;
import com_abertamente_cms.dto.author.AuthorRequest;
import com_abertamente_cms.dto.author.AuthorResponse;
import com_abertamente_cms.repository.AuthorRepository;
import com_abertamente_cms.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Author author;

    @BeforeEach
    void setUp() {
        author = new Author("Jane", "jane@example.com", "Bio", "Dev");
        ReflectionTestUtils.setField(author, "id", UUID.randomUUID());
    }

    @Test
    void shouldCreateAuthorSuccessfully() {
        AuthorRequest request = new AuthorRequest("Jane Doe", "jane.doe@example.com", "New Bio", "New Title", "LinkedIn", "jane.doe");

        when(authorRepository.findByEmail("jane.doe@example.com")).thenReturn(Optional.empty());
        when(authorRepository.save(any(Author.class))).thenAnswer(i -> {
            Author saved = i.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", UUID.randomUUID());
            return saved;
        });

        AuthorResponse response = authorService.create(request);

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals("Jane Doe", response.name());
        assertEquals("jane.doe@example.com", response.email());
        assertEquals("New Bio", response.bio());
        assertEquals("New Title", response.mainTitle());
        assertEquals("LinkedIn", response.preferredSocialNetwork());
        assertEquals("jane.doe", response.preferredSocialNetworkUsername());

        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void shouldThrowExceptionWhenAuthorAlreadyExists() {
        AuthorRequest request = new AuthorRequest("Jane", "jane@example.com", "New Bio", "New Title", null, null);

        when(authorRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(author));

        assertThrows(IllegalArgumentException.class, () -> authorService.create(request));
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void shouldGetAuthorByIdSuccessfully() {
        when(authorRepository.findById(author.getId())).thenReturn(Optional.of(author));

        AuthorResponse response = authorService.getAuthorById(author.getId());

        assertNotNull(response);
        assertEquals(author.getId(), response.id());
        assertEquals(author.getBio(), response.bio());
        assertEquals(author.getMainTitle(), response.mainTitle());
    }

    @Test
    void shouldReturnNullWhenAuthorNotFound() {
        when(authorRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        AuthorResponse response = authorService.getAuthorById(UUID.randomUUID());

        assertNull(response);
    }
    
    @Test
    void shouldGetAllAuthors() {
        when(authorRepository.findAll()).thenReturn(List.of(author));

        List<AuthorResponse> response = authorService.getAllAuthors();

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        assertEquals(author.getId(), response.get(0).id());
    }
}
