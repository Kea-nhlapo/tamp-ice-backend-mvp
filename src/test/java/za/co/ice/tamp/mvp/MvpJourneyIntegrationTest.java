package za.co.ice.tamp.mvp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import za.co.ice.tamp.domain.TrackingStatus;
import za.co.ice.tamp.repository.AuditEventRepository;

@SpringBootTest
@Transactional
class MvpJourneyIntegrationTest {

    @Autowired
    private MvpService service;

    @Autowired
    private AuditEventRepository audits;

    @Test
    void seededDataCompletesTheCoreJourney() {
        List<Map<String, Object>> matches =
                service.generateMatches("owner@tamp.test", 1L);
        assertFalse(matches.isEmpty());
        Long matchId = ((Number) matches.get(0).get("id")).longValue();
        assertTrue(matches.get(0).get("reasons").toString().contains("Capacity"));

        service.decide("owner@tamp.test", matchId, true, "127.0.0.1", "JUnit");
        Map<String, Object> receipt = service.receipt("owner@tamp.test", matchId);
        assertNotNull(receipt.get("contractId"));

        for (TrackingStatus status : List.of(
                TrackingStatus.DRIVER_ASSIGNED,
                TrackingStatus.EN_ROUTE_TO_PICKUP,
                TrackingStatus.CARGO_COLLECTED,
                TrackingStatus.IN_TRANSIT,
                TrackingStatus.DELIVERED,
                TrackingStatus.COMPLETED)) {
            service.addTracking("transporter@tamp.test", matchId, status, null, null);
        }

        Map<String, Object> rating =
                service.rate("owner@tamp.test", matchId, 5, "Synthetic demo rating");
        assertEquals(5, rating.get("score"));

        Map<String, Object> metrics = service.metrics();
        assertEquals(1L, metrics.get("acceptedMatches"));
        assertEquals(1L, metrics.get("completedTrips"));
        assertTrue(audits.findAll().stream()
                .anyMatch(event -> event.getAction().equals("MATCH_ACCEPTED")));
    }
}
