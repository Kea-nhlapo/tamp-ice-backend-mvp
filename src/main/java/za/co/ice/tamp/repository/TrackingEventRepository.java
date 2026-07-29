package za.co.ice.tamp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.ice.tamp.domain.TrackingEvent;

public interface TrackingEventRepository
        extends JpaRepository<TrackingEvent, Long> {

    List<TrackingEvent> findByMatchIdOrderByRecordedAtAsc(Long matchId);
}