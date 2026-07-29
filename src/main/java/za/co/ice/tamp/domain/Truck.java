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
@Table(name = "trucks")
public class Truck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transporter_id", nullable = false)
    private User transporter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TruckType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal capacity;

    @Column(name = "current_location", nullable = false, length = 100)
    private String currentLocation;

    @Column(name = "availability_start", nullable = false)
    private LocalDateTime availabilityStart;

    @Column(name = "availability_end", nullable = false)
    private LocalDateTime availabilityEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TruckStatus status = TruckStatus.AVAILABLE;

    protected Truck() {
    }

    public Truck(
            User transporter,
            TruckType type,
            BigDecimal capacity,
            String currentLocation,
            LocalDateTime availabilityStart,
            LocalDateTime availabilityEnd) {
        this.transporter = transporter;
        this.type = type;
        this.capacity = capacity;
        this.currentLocation = currentLocation;
        this.availabilityStart = availabilityStart;
        this.availabilityEnd = availabilityEnd;
    }

    public Long getId() {
        return id;
    }

    public User getTransporter() {
        return transporter;
    }

    public TruckType getType() {
        return type;
    }

    public void setType(TruckType type) {
        this.type = type;
    }

    public BigDecimal getCapacity() {
        return capacity;
    }

    public void setCapacity(BigDecimal capacity) {
        this.capacity = capacity;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public LocalDateTime getAvailabilityStart() {
        return availabilityStart;
    }

    public void setAvailabilityStart(LocalDateTime availabilityStart) {
        this.availabilityStart = availabilityStart;
    }

    public LocalDateTime getAvailabilityEnd() {
        return availabilityEnd;
    }

    public void setAvailabilityEnd(LocalDateTime availabilityEnd) {
        this.availabilityEnd = availabilityEnd;
    }

    public TruckStatus getStatus() {
        return status;
    }

    public void setStatus(TruckStatus status) {
        this.status = status;
    }
}