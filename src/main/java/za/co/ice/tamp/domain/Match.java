package za.co.ice.tamp.domain;

import java.math.BigDecimal;
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
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "load_id", nullable = false)
    private Load load;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "truck_id", nullable = false)
    private Truck truck;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score;

    @Column(nullable = false, length = 1000)
    private String reasons;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MatchStatus status = MatchStatus.PROPOSED;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Match() {
    }

    public Match(Load load, Truck truck, BigDecimal score, String reasons) {
        this.load = load;
        this.truck = truck;
        this.score = score;
        this.reasons = reasons;
    }

    public Long getId() {
        return id;
    }

    public Load getLoad() {
        return load;
    }

    public Truck getTruck() {
        return truck;
    }

    public BigDecimal getScore() {
        return score;
    }

    public String getReasons() {
        return reasons;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}