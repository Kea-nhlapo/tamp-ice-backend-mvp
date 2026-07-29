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
@Table(name = "cargo_loads")
public class Load {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 100)
    private String origin;

    @Column(nullable = false, length = 100)
    private String destination;

    @Column(name = "cargo_type", nullable = false, length = 50)
    private String cargoType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal weight;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal volume;

    @Column(name = "pickup_start", nullable = false)
    private LocalDateTime pickupStart;

    @Column(name = "pickup_end", nullable = false)
    private LocalDateTime pickupEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LoadStatus status = LoadStatus.OPEN;

    protected Load() {
    }

    public Load(
            User owner,
            String origin,
            String destination,
            String cargoType,
            BigDecimal weight,
            BigDecimal volume,
            LocalDateTime pickupStart,
            LocalDateTime pickupEnd) {
        this.owner = owner;
        this.origin = origin;
        this.destination = destination;
        this.cargoType = cargoType;
        this.weight = weight;
        this.volume = volume;
        this.pickupStart = pickupStart;
        this.pickupEnd = pickupEnd;
    }

    public Long getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCargoType() {
        return cargoType;
    }

    public void setCargoType(String cargoType) {
        this.cargoType = cargoType;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public LocalDateTime getPickupStart() {
        return pickupStart;
    }

    public void setPickupStart(LocalDateTime pickupStart) {
        this.pickupStart = pickupStart;
    }

    public LocalDateTime getPickupEnd() {
        return pickupEnd;
    }

    public void setPickupEnd(LocalDateTime pickupEnd) {
        this.pickupEnd = pickupEnd;
    }

    public LoadStatus getStatus() {
        return status;
    }

    public void setStatus(LoadStatus status) {
        this.status = status;
    }
}