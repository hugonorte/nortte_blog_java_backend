package com_abertamente_cms.repository;

import com_abertamente_cms.domain.Footnote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FootnoteRepository extends JpaRepository<Footnote, UUID> {
    List<Footnote> findByPostIdOrderByCreatedAtAsc(UUID postId);
}
