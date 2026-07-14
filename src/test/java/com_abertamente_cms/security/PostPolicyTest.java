package com_abertamente_cms.security;

import com_abertamente_cms.domain.Post;
import com_abertamente_cms.domain.Role;
import com_abertamente_cms.domain.User;
import com_abertamente_cms.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostPolicyTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private PostPolicy postPolicy;

    private User userAuthor;
    private com_abertamente_cms.domain.Author author;
    private User otherUser;
    private Post post;
    private UUID postId;

    @BeforeEach
    void setUp() {
        userAuthor = new User("Author", "author@example.com", "pass");
        ReflectionTestUtils.setField(userAuthor, "id", UUID.randomUUID());
        
        author = new com_abertamente_cms.domain.Author(userAuthor, "bio", "title");
        ReflectionTestUtils.setField(author, "id", UUID.randomUUID());

        otherUser = new User("Other", "other@example.com", "pass");
        ReflectionTestUtils.setField(otherUser, "id", UUID.randomUUID());

        post = new Post();
        post.setAuthor(author);
        postId = UUID.randomUUID();
        ReflectionTestUtils.setField(post, "id", postId);
    }

    @Test
    void canManage_Admin_ShouldReturnTrue() {
        when(authentication.isAuthenticated()).thenReturn(true);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();

        assertTrue(postPolicy.canManage(authentication, postId));
    }

    @Test
    void canManage_Editor_ShouldReturnTrue() {
        when(authentication.isAuthenticated()).thenReturn(true);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_EDITOR")))
                .when(authentication).getAuthorities();

        assertTrue(postPolicy.canManage(authentication, postId));
    }

    @Test
    void canManage_AuthorIsOwner_ShouldReturnTrue() {
        when(authentication.isAuthenticated()).thenReturn(true);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_AUTHOR")))
                .when(authentication).getAuthorities();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(authentication.getPrincipal()).thenReturn(userAuthor);

        assertTrue(postPolicy.canManage(authentication, postId));
    }

    @Test
    void canManage_AuthorIsNotOwner_ShouldReturnFalse() {
        when(authentication.isAuthenticated()).thenReturn(true);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_AUTHOR")))
                .when(authentication).getAuthorities();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(authentication.getPrincipal()).thenReturn(otherUser);

        assertFalse(postPolicy.canManage(authentication, postId));
    }

    @Test
    void canChangeStatus_Admin_ShouldReturnTrue() {
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();

        assertTrue(postPolicy.canChangeStatus(authentication));
    }

    @Test
    void canChangeStatus_Author_ShouldReturnFalse() {
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_AUTHOR")))
                .when(authentication).getAuthorities();

        assertFalse(postPolicy.canChangeStatus(authentication));
    }
}
