package za.co.ice.tamp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.ice.tamp.domain.Dispute;

public interface DisputeRepository extends JpaRepository<Dispute, Long> {
}