package za.co.ice.tamp.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "ratings",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_rating_match_reviewer",
                columnNames = {"match_id", "reviewer_id"}))
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewed_user_id", nullable = false)
    private User reviewedUser;

    @Column(nullable = false)
    private Integer score;

    @Column(length = 500)
    private String comment;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Rating() {
    }

    public Rating(
            Match match,
            User reviewer,
            User reviewedUser,
            Integer score,
            String comment) {
        this.match = match;
        this.reviewer = reviewer;
        this.reviewedUser = reviewedUser;
        this.score = score;
        this.comment = comment;
    }

    public Long getId() {
        return id;
    }

    public Match getMatch() {
        return match;
    }

    public User getReviewer() {
        return reviewer;
    }

    public User getReviewedUser() {
        return reviewedUser;
    }

    public Integer getScore() {
        return score;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
