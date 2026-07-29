package za.co.ice.tamp.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "compliance_documents")
public class ComplianceDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "document_type", nullable = false, length = 50)
    private String documentType;

    @Column(name = "document_reference", nullable = false, length = 255)
    private String documentReference;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ComplianceStatus status = ComplianceStatus.PENDING;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    protected ComplianceDocument() {
    }

    public ComplianceDocument(User user, String documentType, String documentReference) {
        this.user = user;
        this.documentType = documentType;
        this.documentReference = documentReference;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getDocumentReference() {
        return documentReference;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public ComplianceStatus getStatus() {
        return status;
    }

    public void setStatus(ComplianceStatus status) {
        this.status = status;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}