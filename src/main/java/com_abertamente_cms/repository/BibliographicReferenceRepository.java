package com_abertamente_cms.repository;

import com_abertamente_cms.domain.BibliographicReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BibliographicReferenceRepository extends JpaRepository<BibliographicReference, UUID> {
    List<BibliographicReference> findByPostIdOrderByCreatedAtAsc(UUID postId);
}
