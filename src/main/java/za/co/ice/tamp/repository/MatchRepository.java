package za.co.ice.tamp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.ice.tamp.domain.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByLoadId(Long loadId);

    Optional<Match> findByLoadIdAndTruckId(Long loadId, Long truckId);
}
