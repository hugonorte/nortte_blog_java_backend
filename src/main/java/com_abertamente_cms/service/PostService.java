package com_abertamente_cms.service;

import com_abertamente_cms.domain.Category;
import com_abertamente_cms.domain.Post;
import com_abertamente_cms.domain.PostStatus;
import com_abertamente_cms.domain.User;
import com_abertamente_cms.dto.post.PostRequest;
import com_abertamente_cms.dto.post.PostResponse;
import com_abertamente_cms.exception.ResourceNotFoundException;
import com_abertamente_cms.repository.AuthorRepository;
import com_abertamente_cms.repository.CategoryRepository;
import com_abertamente_cms.repository.PostRepository;
import com_abertamente_cms.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;

    public PostService(PostRepository postRepository, CategoryRepository categoryRepository, UserRepository userRepository, AuthorRepository authorRepository) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true)
    public Page<PostResponse> findAll(PostStatus status, Pageable pageable) {
        if (status != null) {
            return postRepository.findByStatus(status, pageable).map(PostResponse::fromEntity);
        }
        return postRepository.findAll(pageable).map(PostResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public PostResponse findById(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artigo não encontrado."));
        return PostResponse.fromEntity(post);
    }

    @Transactional
    public PostResponse create(PostRequest request) {
        if (postRepository.findBySlug(request.slug()).isPresent()) {
            throw new IllegalArgumentException("Já existe um artigo com este slug.");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        com_abertamente_cms.domain.Author author = authorRepository.findById(request.authorId())
                .orElseThrow(() -> new ResourceNotFoundException("Autor não encontrado."));

        Post post = new Post(request.title(), request.slug(), request.content(), request.tldr(), author, category, request.publishedAt());
        post.setImagePath(request.imagePath());
        post = postRepository.save(post);

        return PostResponse.fromEntity(post);
    }

    @Transactional
    @PreAuthorize("@postPolicy.canManage(authentication, #id)")
    public PostResponse update(UUID id, PostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artigo não encontrado."));

        if (!post.getSlug().equals(request.slug()) && postRepository.findBySlug(request.slug()).isPresent()) {
            throw new IllegalArgumentException("Slug do artigo já está em uso.");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada."));

        post.setTitle(request.title());
        post.setSlug(request.slug());
        post.setContent(request.content());
        post.setTldr(request.tldr());
        post.setImagePath(request.imagePath());
        post.setCategory(category);
        post.setPublishedAt(request.publishedAt());

        return PostResponse.fromEntity(postRepository.save(post));
    }

    @Transactional
    @PreAuthorize("@postPolicy.canManage(authentication, #id)")
    public void delete(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artigo não encontrado."));
        postRepository.delete(post);
    }

    @Transactional
    @PreAuthorize("@postPolicy.canChangeStatus(authentication)")
    public PostResponse changeStatus(UUID id, PostStatus status) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artigo não encontrado."));
        
        post.setStatus(status);
        return PostResponse.fromEntity(postRepository.save(post));
    }
}
