package za.co.ice.tamp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import za.co.ice.tamp.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);
}