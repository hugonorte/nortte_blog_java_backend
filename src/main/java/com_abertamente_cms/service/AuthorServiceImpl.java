package com_abertamente_cms.service;

import com_abertamente_cms.domain.Author;
import com_abertamente_cms.domain.User;
import com_abertamente_cms.dto.author.AuthorRequest;
import com_abertamente_cms.dto.author.AuthorResponse;
import com_abertamente_cms.repository.AuthorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    @Transactional
    public AuthorResponse create(AuthorRequest request) {
        if (authorRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Author profile already exists with this email.");
        }

        Author author = new Author(request.name(), request.email(), request.bio(), request.mainTitle());
        author.setPreferredSocialNetwork(request.preferredSocialNetwork());
        author.setPreferredSocialNetworkUsername(request.preferredSocialNetworkUsername());

        author = authorRepository.save(author);
        return AuthorResponse.fromEntity(author);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponse getAuthorById(UUID id) {
        Author author = authorRepository.findById(id).orElse(null);
        return AuthorResponse.fromEntity(author);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AuthorResponse> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(AuthorResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
