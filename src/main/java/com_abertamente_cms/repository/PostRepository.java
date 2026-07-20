package com_abertamente_cms.repository;

import com_abertamente_cms.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query(value = "SELECT * FROM posts p " +
            "WHERE p.search_vector @@ plainto_tsquery('portuguese', :query) " +
            "AND p.status = 'PUBLISHED' " +
            "ORDER BY ts_rank(p.search_vector, plainto_tsquery('portuguese', :query)) DESC",
            countQuery = "SELECT count(*) FROM posts p WHERE p.search_vector @@ plainto_tsquery('portuguese', :query) AND p.status = 'PUBLISHED'",
            nativeQuery = true)
    Page<Post> searchPosts(@org.springframework.data.repository.query.Param("query") String query, Pageable pageable);
}
