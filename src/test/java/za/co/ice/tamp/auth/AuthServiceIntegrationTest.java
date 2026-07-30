package za.co.ice.tamp.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import za.co.ice.tamp.domain.User;
import za.co.ice.tamp.domain.UserRole;
import za.co.ice.tamp.repository.AuditEventRepository;
import za.co.ice.tamp.repository.UserRepository;

@SpringBootTest
@Transactional
class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditEventRepository auditEventRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void freightOwnerCanRegisterAndPasswordIsProtected() {
        AuthResponse response = authService.register(new RegisterRequest(
                "New Owner",
                "registered-owner@tamp.test",
                "Password123!",
                UserRole.FREIGHT_OWNER
        ));

        User savedUser = userRepository
                .findByEmailIgnoreCase("registered-owner@tamp.test")
                .orElseThrow();

        assertEquals(UserRole.FREIGHT_OWNER, response.role());
        assertFalse(response.token().isBlank());
        assertNotEquals("Password123!", savedUser.getPasswordHash());
        assertTrue(passwordEncoder.matches(
                "Password123!",
                savedUser.getPasswordHash()
        ));
        assertTrue(auditEventRepository.findAll().stream()
                .anyMatch(event -> event.getAction().equals("USER_REGISTERED")
                        && event.getActor().getId().equals(savedUser.getId())));
    }

    @Test
    void transporterCanRegister() {
        AuthResponse response = authService.register(new RegisterRequest(
                "New Transporter",
                "registered-transporter@tamp.test",
                "Password123!",
                UserRole.TRANSPORTER
        ));

        assertEquals(UserRole.TRANSPORTER, response.role());
    }

    @Test
    void publicAdminRegistrationIsRejected() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(new RegisterRequest(
                        "Fake Admin",
                        "fake-admin@tamp.test",
                        "Password123!",
                        UserRole.ADMIN
                ))
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void duplicateEmailIsRejected() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(new RegisterRequest(
                        "Duplicate Owner",
                        "owner@tamp.test",
                        "Password123!",
                        UserRole.FREIGHT_OWNER
                ))
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    void seededAdminCanLogIn() {
        AuthResponse response = authService.login(
                new LoginRequest("admin@tamp.test", "Password123!")
        );

        assertEquals(UserRole.ADMIN, response.role());
        assertFalse(response.token().isBlank());
        assertTrue(auditEventRepository.findAll().stream()
                .anyMatch(event -> event.getAction().equals("USER_LOGGED_IN")
                        && event.getActor().getRole() == UserRole.ADMIN));
    }
}
