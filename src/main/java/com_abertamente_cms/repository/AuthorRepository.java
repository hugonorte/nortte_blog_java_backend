package com_abertamente_cms.repository;

import com_abertamente_cms.domain.Author;
import com_abertamente_cms.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {
    Optional<Author> findByUser(User user);
}
