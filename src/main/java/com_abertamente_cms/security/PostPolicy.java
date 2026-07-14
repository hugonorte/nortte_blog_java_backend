package com_abertamente_cms.security;

import com_abertamente_cms.domain.Post;
import com_abertamente_cms.domain.User;
import com_abertamente_cms.repository.PostRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("postPolicy")
public class PostPolicy {

    private final PostRepository postRepository;

    public PostPolicy(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * Verifica se o usuário tem permissão para editar/deletar um post.
     * ADMIN e EDITOR podem tudo.
     * AUTHOR só pode se for o autor do post.
     */
    public boolean canManage(Authentication auth, UUID postId) {
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }

        if (isAdminOrEditor(auth)) {
            return true;
        }

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return false; // Ou true? Melhor deixar retornar 404 posteriormente na Service
        }

        User currentUser = (User) auth.getPrincipal();
        return post.getAuthor().getUser().getId().equals(currentUser.getId());
    }

    /**
     * Apenas ADMIN ou EDITOR podem alterar o status de um post (publicar, arquivar),
     * a não ser que exista outra regra futura.
     */
    public boolean canChangeStatus(Authentication auth) {
        return isAdminOrEditor(auth);
    }

    private boolean isAdminOrEditor(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_EDITOR"));
    }
}
