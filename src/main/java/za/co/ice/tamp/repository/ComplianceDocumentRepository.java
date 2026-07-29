package za.co.ice.tamp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.ice.tamp.domain.ComplianceDocument;

public interface ComplianceDocumentRepository
        extends JpaRepository<ComplianceDocument, Long> {

    List<ComplianceDocument> findByUserId(Long userId);
}