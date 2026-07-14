package com_abertamente_cms.service;

import com_abertamente_cms.dto.author.AuthorRequest;
import com_abertamente_cms.dto.author.AuthorResponse;

import java.util.List;
import java.util.UUID;

public interface AuthorService {
    AuthorResponse create(AuthorRequest request);
    AuthorResponse getAuthorById(UUID id);
    List<AuthorResponse> getAllAuthors();
}
