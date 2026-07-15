package com_abertamente_cms.controller;

import com_abertamente_cms.dto.post.BibliographicReferenceRequest;
import com_abertamente_cms.dto.post.BibliographicReferenceResponse;
import com_abertamente_cms.dto.post.BibliographicReferenceUpdateRequest;
import com_abertamente_cms.service.BibliographicReferenceService;
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
@RequestMapping("/api/bibliographic_reference")
public class BibliographicReferenceController {

    private final BibliographicReferenceService referenceService;

    public BibliographicReferenceController(BibliographicReferenceService referenceService) {
        this.referenceService = referenceService;
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<BibliographicReferenceResponse>> list(@PathVariable UUID postId) {
        return ResponseEntity.ok(referenceService.findAllByPostId(postId));
    }

    @PostMapping
    public ResponseEntity<BibliographicReferenceResponse> create(@Valid @RequestBody BibliographicReferenceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(referenceService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BibliographicReferenceResponse> update(@PathVariable UUID id, @Valid @RequestBody BibliographicReferenceUpdateRequest request) {
        return ResponseEntity.ok(referenceService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        referenceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
