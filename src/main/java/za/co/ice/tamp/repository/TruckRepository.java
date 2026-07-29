package za.co.ice.tamp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.ice.tamp.domain.Truck;

public interface TruckRepository extends JpaRepository<Truck, Long> {

    List<Truck> findByTransporterId(Long transporterId);
}