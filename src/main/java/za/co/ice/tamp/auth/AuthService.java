package za.co.ice.tamp.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import za.co.ice.tamp.domain.AuditEvent;
import za.co.ice.tamp.domain.User;
import za.co.ice.tamp.domain.UserRole;
import za.co.ice.tamp.repository.AuditEventRepository;
import za.co.ice.tamp.repository.UserRepository;
import za.co.ice.tamp.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuditEventRepository auditEventRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            AuditEventRepository auditEventRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.auditEventRepository = auditEventRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request.role() == UserRole.ADMIN) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Public Admin registration is not allowed"
            );
        }

        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }

        User user = new User(
                request.name().trim(),
                email,
                passwordEncoder.encode(request.password()),
                request.role()
        );

        User savedUser = userRepository.save(user);
        saveAuditEvent(savedUser, "USER_REGISTERED");
        return createResponse(savedUser);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid email or password"
                ));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        saveAuditEvent(user, "USER_LOGGED_IN");
        return createResponse(user);
    }

    private void saveAuditEvent(User user, String action) {
        auditEventRepository.save(new AuditEvent(
                user,
                action,
                "User",
                user.getId().toString(),
                "role=" + user.getRole().name()
        ));
    }

    private AuthResponse createResponse(User user) {
        return new AuthResponse(
                jwtService.generateToken(user),
                "Bearer",
                user.getEmail(),
                user.getRole()
        );
    }
}
