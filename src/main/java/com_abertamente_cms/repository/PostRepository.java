package com_abertamente_cms.repository;

import com_abertamente_cms.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    @EntityGraph(attributePaths = {"author", "category"})
    Optional<Post> findById(UUID id);

    @EntityGraph(attributePaths = {"author", "category"})
    Optional<Post> findBySlug(String slug);

    @EntityGraph(attributePaths = {"author", "category"})
    Page<Post> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"author", "category"})
    Page<Post> findByStatus(com_abertamente_cms.domain.PostStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "category"})
    Page<Post> findByAuthorId(UUID authorId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "category"})
    Page<Post> findByCategoryId(UUID categoryId, Pageable pageable);
}
