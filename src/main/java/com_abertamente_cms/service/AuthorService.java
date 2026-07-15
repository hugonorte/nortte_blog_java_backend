package com_abertamente_cms.service;

import com_abertamente_cms.dto.author.AuthorRequest;
import com_abertamente_cms.dto.author.AuthorResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AuthorService {
    AuthorResponse create(AuthorRequest request);
    AuthorResponse getAuthorById(UUID id);
    Page<AuthorResponse> getAllAuthors(Pageable pageable);
    AuthorResponse update(UUID id, AuthorRequest request);
    void delete(UUID id);
}
