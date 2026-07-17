package com_abertamente_cms.service;

import com_abertamente_cms.domain.Category;
import com_abertamente_cms.domain.ContentFormat;
import com_abertamente_cms.domain.Post;
import com_abertamente_cms.domain.PostStatus;
import com_abertamente_cms.domain.User;
import com_abertamente_cms.dto.post.PostRequest;
import com_abertamente_cms.dto.post.PostResponse;
import com_abertamente_cms.repository.AuthorRepository;
import com_abertamente_cms.repository.CategoryRepository;
import com_abertamente_cms.repository.PostRepository;
import com_abertamente_cms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PostService postService;

    private User user;
    private com_abertamente_cms.domain.Author author;
    private Category category;
    private Post post;
    private UUID postId;

    @BeforeEach
    void setUp() {
        user = new User("Jane", "Doe", "jane@example.com", "pass");
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());

        author = new com_abertamente_cms.domain.Author("Author Name", "author@example.com", "Bio", "Dev");
        ReflectionTestUtils.setField(author, "id", UUID.randomUUID());

        category = new Category("Tech", "tech", "desc");
        ReflectionTestUtils.setField(category, "id", UUID.randomUUID());

        postId = UUID.randomUUID();
        post = new Post("Título", "titulo", "conteudo", "tldr", author, category, java.time.Instant.now());
        ReflectionTestUtils.setField(post, "id", postId);
    }

    @Test
    void shouldCreatePost() {
        PostRequest request = new PostRequest("Título Novo", "titulo-novo", "conteudo", ContentFormat.HTML, "tldr", "/uploads/posts/cover.jpg", java.time.Instant.now(), category.getId(), author.getId());

        when(authorRepository.findById(author.getId())).thenReturn(Optional.of(author));
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(postRepository.findBySlug(request.slug())).thenReturn(Optional.empty());
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        PostResponse response = postService.create(request);

        assertNotNull(response);
        assertEquals("Título Novo", response.title());
        assertEquals("titulo-novo", response.slug());
    }

    @Test
    void shouldUpdatePost() {
        PostRequest request = new PostRequest("Título Edit", "titulo-edit", "conteudo edit", ContentFormat.HTML, "tldr edit", "/uploads/posts/cover.jpg", java.time.Instant.now(), category.getId(), author.getId());

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.findBySlug(request.slug())).thenReturn(Optional.empty());
        when(categoryRepository.findById(category.getId())).thenReturn(Optional.of(category));
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        PostResponse response = postService.update(postId, request);

        assertEquals("Título Edit", response.title());
    }

    @Test
    void shouldDeletePost() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        postService.delete(postId);
        verify(postRepository).delete(post);
    }

    @Test
    void shouldChangeStatus() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenAnswer(i -> i.getArgument(0));

        PostResponse response = postService.changeStatus(postId, PostStatus.PUBLISHED);

        assertEquals(PostStatus.PUBLISHED, response.status());
    }
}
