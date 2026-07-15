package com_abertamente_cms.controller;

import com_abertamente_cms.dto.post.FootnoteRequest;
import com_abertamente_cms.dto.post.FootnoteResponse;
import com_abertamente_cms.dto.post.FootnoteUpdateRequest;
import com_abertamente_cms.service.FootnoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/footnote")
public class FootnoteController {

    private final FootnoteService footnoteService;

    public FootnoteController(FootnoteService footnoteService) {
        this.footnoteService = footnoteService;
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<FootnoteResponse>> list(@PathVariable UUID postId) {
        return ResponseEntity.ok(footnoteService.findAllByPostId(postId));
    }

    @PostMapping
    public ResponseEntity<FootnoteResponse> create(@Valid @RequestBody FootnoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(footnoteService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FootnoteResponse> update(@PathVariable UUID id, @Valid @RequestBody FootnoteUpdateRequest request) {
        return ResponseEntity.ok(footnoteService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        footnoteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
