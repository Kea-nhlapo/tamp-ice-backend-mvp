package za.co.ice.tamp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.ice.tamp.domain.AuditEvent;

public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
}