package com_abertamente_cms.service;

import com_abertamente_cms.domain.BibliographicReference;
import com_abertamente_cms.domain.Post;
import com_abertamente_cms.dto.post.BibliographicReferenceRequest;
import com_abertamente_cms.dto.post.BibliographicReferenceResponse;
import com_abertamente_cms.dto.post.BibliographicReferenceUpdateRequest;
import com_abertamente_cms.exception.ResourceNotFoundException;
import com_abertamente_cms.repository.BibliographicReferenceRepository;
import com_abertamente_cms.repository.PostRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BibliographicReferenceService {

    private final BibliographicReferenceRepository referenceRepository;
    private final PostRepository postRepository;

    public BibliographicReferenceService(BibliographicReferenceRepository referenceRepository, PostRepository postRepository) {
        this.referenceRepository = referenceRepository;
        this.postRepository = postRepository;
    }

    @Transactional(readOnly = true)
    public List<BibliographicReferenceResponse> findAllByPostId(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Artigo não encontrado.");
        }
        return referenceRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(BibliographicReferenceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("@postPolicy.canManage(authentication, #request.post_id())")
    public BibliographicReferenceResponse create(BibliographicReferenceRequest request) {
        Post post = postRepository.findById(request.post_id())
                .orElseThrow(() -> new ResourceNotFoundException("Artigo não encontrado."));

        BibliographicReference reference = new BibliographicReference(post, request.description());
        reference = referenceRepository.save(reference);

        return BibliographicReferenceResponse.fromEntity(reference);
    }

    @Transactional
    @PreAuthorize("@postPolicy.canManage(authentication, @bibliographicReferenceService.getPostIdForReference(#request.id()))")
    public BibliographicReferenceResponse update(BibliographicReferenceUpdateRequest request) {
        BibliographicReference reference = referenceRepository.findById(request.id())
                .orElseThrow(() -> new ResourceNotFoundException("Referência não encontrada."));

        reference.setDescription(request.description());
        return BibliographicReferenceResponse.fromEntity(referenceRepository.save(reference));
    }

    @Transactional
    @PreAuthorize("@postPolicy.canManage(authentication, @bibliographicReferenceService.getPostIdForReference(#id))")
    public void delete(UUID id) {
        BibliographicReference reference = referenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Referência não encontrada."));

        referenceRepository.delete(reference);
    }

    @Transactional(readOnly = true)
    public UUID getPostIdForReference(UUID id) {
        return referenceRepository.findById(id)
                .map(r -> r.getPost().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Referência não encontrada."));
    }
}
