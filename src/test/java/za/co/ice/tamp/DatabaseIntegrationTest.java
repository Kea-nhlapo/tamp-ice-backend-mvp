package za.co.ice.tamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import za.co.ice.tamp.domain.Load;
import za.co.ice.tamp.domain.Truck;
import za.co.ice.tamp.domain.UserRole;
import za.co.ice.tamp.repository.LoadRepository;
import za.co.ice.tamp.repository.TruckRepository;

@SpringBootTest
class DatabaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private LoadRepository loadRepository;

    @Autowired
    private TruckRepository truckRepository;

    @Test
    void migrationsCreateTablesAndSeedSyntheticUsers() {
        Integer userCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users",
                Integer.class
        );

        Integer adminCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE role = 'ADMIN'",
                Integer.class
        );

        assertEquals(3, userCount);
        assertEquals(1, adminCount);
    }

    @Test
    @Transactional
    void seededLoadsAndTrucksBelongToCorrectUserRoles() {
        Load load = loadRepository.findAll().get(0);
        Truck truck = truckRepository.findAll().get(0);

        assertEquals(UserRole.FREIGHT_OWNER, load.getOwner().getRole());
        assertEquals(UserRole.TRANSPORTER, truck.getTransporter().getRole());
    }

    @Test
    @Transactional
    void nonPositiveLoadWeightIsRejected() {
        assertThrows(
                DataIntegrityViolationException.class,
                () -> jdbcTemplate.update("UPDATE cargo_loads SET weight = 0")
        );
    }

    @Test
    @Transactional
    void duplicateUserEmailIsRejected() {
        assertThrows(
                DataIntegrityViolationException.class,
                () -> jdbcTemplate.update("""
                        UPDATE users
                        SET email = (
                            SELECT email
                            FROM users
                            WHERE role = 'ADMIN'
                        )
                        WHERE role = 'FREIGHT_OWNER'
                        """)
        );
    }
}
