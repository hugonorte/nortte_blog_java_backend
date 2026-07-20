package com_abertamente_cms.controller;

import com_abertamente_cms.domain.PostStatus;
import com_abertamente_cms.dto.post.PostRequest;
import com_abertamente_cms.dto.post.PostResponse;
import com_abertamente_cms.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/post")
@org.springframework.validation.annotation.Validated
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostResponse>> search(
            @RequestParam @jakarta.validation.constraints.Size(min = 3, message = "A busca deve ter pelo menos 3 caracteres") String query,
            @RequestParam(required = false, name = "contact_email") String honeypot,
            Pageable pageable) {
        return ResponseEntity.ok(postService.searchPosts(query, honeypot, pageable));
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> index(
            @RequestParam(required = false) PostStatus status, 
            Pageable pageable) {
        return ResponseEntity.ok(postService.findAll(status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> show(@PathVariable UUID id) {
        return ResponseEntity.ok(postService.findById(id));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<com_abertamente_cms.dto.post.PostContentResponse> showBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(postService.findContentBySlug(slug));
    }

    @PostMapping
    public ResponseEntity<PostResponse> store(@Valid @RequestBody PostRequest request) {
        PostResponse response = postService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> update(@PathVariable UUID id, @Valid @RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable UUID id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PostResponse> changeStatus(@PathVariable UUID id, @RequestParam PostStatus status) {
        return ResponseEntity.ok(postService.changeStatus(id, status));
    }

    @PatchMapping("/{id}/content")
    public ResponseEntity<PostResponse> updateContent(
            @PathVariable UUID id, 
            @Valid @RequestBody com_abertamente_cms.dto.post.PostContentUpdateRequest request) {
        return ResponseEntity.ok(postService.updateContent(id, request));
    }
}
