package za.co.ice.tamp.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import za.co.ice.tamp.auth.AuthResponse;
import za.co.ice.tamp.auth.AuthService;
import za.co.ice.tamp.auth.RegisterRequest;
import za.co.ice.tamp.domain.UserRole;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Test
    void missingTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/test"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/test")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void freightOwnerCannotAccessAdminEndpoints() throws Exception {
        AuthResponse response = authService.register(new RegisterRequest(
                "Security Test Owner",
                "security-owner@tamp.test",
                "Password123!",
                UserRole.FREIGHT_OWNER
        ));

        mockMvc.perform(get("/api/admin/test")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + response.token()
                        ))
                .andExpect(status().isForbidden());
    }
}
