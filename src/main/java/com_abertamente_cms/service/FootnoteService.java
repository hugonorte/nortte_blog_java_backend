package com_abertamente_cms.service;

import com_abertamente_cms.domain.Footnote;
import com_abertamente_cms.domain.Post;
import com_abertamente_cms.dto.post.FootnoteRequest;
import com_abertamente_cms.dto.post.FootnoteResponse;
import com_abertamente_cms.dto.post.FootnoteUpdateRequest;
import com_abertamente_cms.exception.ResourceNotFoundException;
import com_abertamente_cms.repository.FootnoteRepository;
import com_abertamente_cms.repository.PostRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FootnoteService {

    private final FootnoteRepository footnoteRepository;
    private final PostRepository postRepository;

    public FootnoteService(FootnoteRepository footnoteRepository, PostRepository postRepository) {
        this.footnoteRepository = footnoteRepository;
        this.postRepository = postRepository;
    }

    @Transactional(readOnly = true)
    public List<FootnoteResponse> findAllByPostId(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Artigo não encontrado.");
        }
        return footnoteRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(FootnoteResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    @PreAuthorize("@postPolicy.canManage(authentication, #request.post_id())")
    public FootnoteResponse create(FootnoteRequest request) {
        Post post = postRepository.findById(request.post_id())
                .orElseThrow(() -> new ResourceNotFoundException("Artigo não encontrado."));

        Footnote footnote = new Footnote(post, request.description());
        footnote = footnoteRepository.save(footnote);

        return FootnoteResponse.fromEntity(footnote);
    }

    @Transactional
    @PreAuthorize("@postPolicy.canManage(authentication, @footnoteService.getPostIdForFootnote(#request.id()))")
    public FootnoteResponse update(FootnoteUpdateRequest request) {
        Footnote footnote = footnoteRepository.findById(request.id())
                .orElseThrow(() -> new ResourceNotFoundException("Nota de rodapé não encontrada."));

        footnote.setDescription(request.description());
        return FootnoteResponse.fromEntity(footnoteRepository.save(footnote));
    }

    @Transactional
    @PreAuthorize("@postPolicy.canManage(authentication, @footnoteService.getPostIdForFootnote(#id))")
    public void delete(UUID id) {
        Footnote footnote = footnoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nota de rodapé não encontrada."));

        footnoteRepository.delete(footnote);
    }

    @Transactional(readOnly = true)
    public UUID getPostIdForFootnote(UUID id) {
        return footnoteRepository.findById(id)
                .map(f -> f.getPost().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Nota de rodapé não encontrada."));
    }
}
