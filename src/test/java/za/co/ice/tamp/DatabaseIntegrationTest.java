package za.co.ice.tamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class DatabaseIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
    void seededLoadsAndTrucksBelongToCorrectUserRoles() {
        Integer validLoadOwners = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM cargo_loads load
                JOIN users owner ON owner.id = load.owner_id
                WHERE owner.role = 'FREIGHT_OWNER'
                """, Integer.class);

        Integer validTruckOwners = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM trucks truck
                JOIN users transporter ON transporter.id = truck.transporter_id
                WHERE transporter.role = 'TRANSPORTER'
                """, Integer.class);

        assertEquals(1, validLoadOwners);
        assertEquals(1, validTruckOwners);
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
