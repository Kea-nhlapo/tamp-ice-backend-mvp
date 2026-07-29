package za.co.ice.tamp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.ice.tamp.domain.Receipt;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    Optional<Receipt> findByMatchId(Long matchId);

    Optional<Receipt> findByContractId(String contractId);
}