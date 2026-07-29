package za.co.ice.tamp.auth;

import za.co.ice.tamp.domain.UserRole;

public record AuthResponse(
        String token,
        String tokenType,
        String email,
        UserRole role) {
}