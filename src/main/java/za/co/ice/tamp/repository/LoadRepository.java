package za.co.ice.tamp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.ice.tamp.domain.Load;

public interface LoadRepository extends JpaRepository<Load, Long> {

    List<Load> findByOwnerId(Long ownerId);
}