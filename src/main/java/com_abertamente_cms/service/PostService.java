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
import com_abertamente_cms.domain.ContentFormat;
import com_abertamente_cms.dto.post.PostContentUpdateRequest;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

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
            return postRepository.findByStatus(status, pageable).map(this::toPostResponse);
        }
        return postRepository.findAll(pageable).map(this::toPostResponse);
    }

    @Transactional(readOnly = true)
    public PostResponse findById(UUID id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artigo não encontrado."));
        return toPostResponse(post);
    }

    @Transactional(readOnly = true)
    public PostResponse findBySlug(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Artigo não encontrado."));
        return toPostResponse(post);
    }

    @Transactional(readOnly = true)
    public com_abertamente_cms.dto.post.PostContentResponse findContentBySlug(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Artigo não encontrado."));
        return com_abertamente_cms.dto.post.PostContentResponse.fromEntity(post, processHtmlContent(post));
    }
    @Transactional(readOnly = true)
    public Page<PostResponse> searchPosts(String query, String honeypot, Pageable pageable) {
        if (honeypot != null && !honeypot.trim().isEmpty()) {
            return Page.empty(pageable); // Honeypot preenchido, é um bot
        }
        return postRepository.searchPosts(query, pageable).map(this::toPostResponse);
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

        String safeContent = sanitizeInputIfHtml(request.content(), request.formatType());
        Post post = new Post(request.title(), request.slug(), safeContent, request.tldr(), author, category, request.publishedAt());
        post.setFormatType(request.formatType());
        post.setImagePath(request.imagePath());
        post = postRepository.save(post);

        return toPostResponse(post);
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
        post.setContent(sanitizeInputIfHtml(request.content(), request.formatType()));
        post.setFormatType(request.formatType());
        post.setTldr(request.tldr());
        post.setImagePath(request.imagePath());
        post.setCategory(category);
        post.setPublishedAt(request.publishedAt());

        return toPostResponse(postRepository.save(post));
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
        return toPostResponse(postRepository.save(post));
    }
    
    @Transactional
    @PreAuthorize("@postPolicy.canManage(authentication, #id)")
    public PostResponse updateContent(UUID id, PostContentUpdateRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artigo não encontrado."));
        
        post.setFormatType(request.formatType());
        post.setContent(sanitizeInputIfHtml(request.content(), request.formatType()));
        return toPostResponse(postRepository.save(post));
    }

    private String sanitizeInputIfHtml(String content, ContentFormat formatType) {
        if (content == null || content.isEmpty()) return content;
        if (formatType == ContentFormat.HTML) {
            PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS).and(Sanitizers.BLOCKS).and(Sanitizers.IMAGES).and(Sanitizers.STYLES).and(Sanitizers.TABLES);
            return policy.sanitize(content);
        }
        return content;
    }

    private String processHtmlContent(Post post) {
        String rawContent = post.getContent();
        if (rawContent == null || rawContent.isEmpty()) {
            return "";
        }
        
        String html;
        if (post.getFormatType() == ContentFormat.MARKDOWN) {
            Parser parser = Parser.builder().build();
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            html = renderer.render(parser.parse(rawContent));
        } else {
            html = rawContent;
        }

        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS).and(Sanitizers.BLOCKS).and(Sanitizers.IMAGES).and(Sanitizers.STYLES).and(Sanitizers.TABLES);
        return policy.sanitize(html);
    }
    
    private PostResponse toPostResponse(Post post) {
        String processedHtml = processHtmlContent(post);
        return PostResponse.fromEntity(post, processedHtml);
    }
}
