package com_abertamente_cms.service;

import com_abertamente_cms.domain.Author;
import com_abertamente_cms.domain.User;
import com_abertamente_cms.dto.author.AuthorRequest;
import com_abertamente_cms.dto.author.AuthorResponse;
import com_abertamente_cms.repository.AuthorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

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
    public Page<AuthorResponse> getAllAuthors(Pageable pageable) {
        return authorRepository.findAll(pageable).map(AuthorResponse::fromEntity);
    }

    @Override
    @Transactional
    public AuthorResponse update(UUID id, AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author not found."));

        if (!author.getEmail().equals(request.email()) && authorRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Author profile already exists with this email.");
        }

        if (request.name() != null) author.setName(request.name());
        if (request.email() != null) author.setEmail(request.email());
        if (request.bio() != null) author.setBio(request.bio());
        if (request.mainTitle() != null) author.setMainTitle(request.mainTitle());
        if (request.preferredSocialNetwork() != null) author.setPreferredSocialNetwork(request.preferredSocialNetwork());
        if (request.preferredSocialNetworkUsername() != null) author.setPreferredSocialNetworkUsername(request.preferredSocialNetworkUsername());

        author = authorRepository.save(author);
        return AuthorResponse.fromEntity(author);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author not found."));
        authorRepository.delete(author);
    }
}
