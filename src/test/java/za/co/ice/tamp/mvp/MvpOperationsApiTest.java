package za.co.ice.tamp.mvp;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import za.co.ice.tamp.auth.AuthService;
import za.co.ice.tamp.auth.LoginRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MvpOperationsApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    private String ownerToken;
    private String transporterToken;

    @BeforeEach
    void logInSyntheticUsers() {
        ownerToken = authService.login(
                new LoginRequest("owner@tamp.test", "Password123!")).token();
        transporterToken = authService.login(
                new LoginRequest("transporter@tamp.test", "Password123!")).token();
    }

    @Test
    void freightOwnerCanCreateLoad() throws Exception {
        mockMvc.perform(post("/api/loads")
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoad()))
                .andExpect(status().isCreated());
    }

    @Test
    void transporterCannotCreateLoad() throws Exception {
        mockMvc.perform(post("/api/loads")
                        .header(HttpHeaders.AUTHORIZATION, bearer(transporterToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoad()))
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidLoadWeightIsRejected() throws Exception {
        mockMvc.perform(post("/api/loads")
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validLoad().replace("\"5000\"", "\"0\"")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void transporterCanCreateTruck() throws Exception {
        mockMvc.perform(post("/api/trucks")
                        .header(HttpHeaders.AUTHORIZATION, bearer(transporterToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTruck()))
                .andExpect(status().isCreated());
    }

    @Test
    void freightOwnerCannotCreateTruck() throws Exception {
        mockMvc.perform(post("/api/trucks")
                        .header(HttpHeaders.AUTHORIZATION, bearer(ownerToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validTruck()))
                .andExpect(status().isForbidden());
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String validLoad() {
        return """
                {
                  "origin": "Johannesburg",
                  "destination": "Durban",
                  "cargoType": "GENERAL",
                  "weight": "5000",
                  "volume": "25",
                  "pickupStart": "2026-07-30T08:00:00",
                  "pickupEnd": "2026-07-30T17:00:00"
                }
                """;
    }

    private String validTruck() {
        return """
                {
                  "type": "BOX_TRUCK",
                  "capacity": "8000",
                  "currentLocation": "Johannesburg",
                  "availabilityStart": "2026-07-30T06:00:00",
                  "availabilityEnd": "2026-07-30T20:00:00"
                }
                """;
    }
}
