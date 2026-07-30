package za.co.ice.tamp.mvp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import za.co.ice.tamp.domain.TrackingStatus;
import za.co.ice.tamp.domain.TruckType;
import za.co.ice.tamp.repository.AuditEventRepository;

@SpringBootTest
@Transactional
class MvpJourneyIntegrationTest {

    private static final String OWNER = "owner@tamp.test";
    private static final String TRANSPORTER = "transporter@tamp.test";
    private static final LocalDateTime START = LocalDateTime.of(2026, 7, 30, 8, 0);

    @Autowired
    private MvpService service;

    @Autowired
    private AuditEventRepository audits;

    @Test
    void seededDataCompletesTheCoreJourney() {
        Long matchId = acceptSeedMatch();
        Map<String, Object> receipt = service.receipt(OWNER, matchId);
        assertNotNull(receipt.get("contractId"));

        completeTrip(matchId);
        Map<String, Object> rating = service.rate(OWNER, matchId, 5, "Synthetic demo rating");
        assertEquals(5, rating.get("score"));

        Map<String, Object> metrics = service.metrics();
        assertEquals(1L, metrics.get("acceptedMatches"));
        assertEquals(1L, metrics.get("completedTrips"));
        assertTrue(audits.findAll().stream()
                .anyMatch(event -> event.getAction().equals("MATCH_ACCEPTED")));
    }

    @Test
    void matchedTruckCannotBeMatchedToAnotherLoad() {
        acceptSeedMatch();
        Map<String, Object> secondLoad = service.createLoad(
                OWNER, "Johannesburg", "Durban", "GENERAL",
                new BigDecimal("1000"), new BigDecimal("10"), START, START.plusHours(8));
        Long secondLoadId = ((Number) secondLoad.get("id")).longValue();

        assertTrue(service.generateMatches(OWNER, secondLoadId).isEmpty());
    }

    @Test
    void staleProposalCannotDoubleBookATruck() {
        List<Map<String, Object>> firstProposals = service.generateMatches(OWNER, 1L);
        Map<String, Object> secondLoad = service.createLoad(
                OWNER, "Johannesburg", "Durban", "GENERAL",
                new BigDecimal("1000"), new BigDecimal("10"), START, START.plusHours(8));
        Long secondLoadId = ((Number) secondLoad.get("id")).longValue();
        List<Map<String, Object>> secondProposals = service.generateMatches(OWNER, secondLoadId);

        assertFalse(firstProposals.isEmpty());
        assertFalse(secondProposals.isEmpty());
        Long firstMatchId = ((Number) firstProposals.get(0).get("id")).longValue();
        Long secondMatchId = ((Number) secondProposals.get(0).get("id")).longValue();

        service.decide(OWNER, firstMatchId, true, "127.0.0.1", "JUnit");
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.decide(
                        OWNER, secondMatchId, true, "127.0.0.1", "JUnit"));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    void nonOpenLoadCannotGenerateNewMatches() {
        acceptSeedMatch();

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.generateMatches(OWNER, 1L));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    void administratorCannotRateATrip() {
        Long matchId = acceptSeedMatch();
        completeTrip(matchId);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.rate("admin@tamp.test", matchId, 5, "Not allowed"));
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void acceptedLoadAndTruckCannotBeEdited() {
        acceptSeedMatch();

        assertEquals(HttpStatus.CONFLICT, assertThrows(
                ResponseStatusException.class,
                () -> service.updateLoad(
                        OWNER, 1L, "Changed", "Durban", "GENERAL",
                        new BigDecimal("5000"), new BigDecimal("25"),
                        START, START.plusHours(8))).getStatusCode());
        assertEquals(HttpStatus.CONFLICT, assertThrows(
                ResponseStatusException.class,
                () -> service.updateTruck(
                        TRANSPORTER, 1L, TruckType.BOX_TRUCK,
                        new BigDecimal("9000"), "Johannesburg",
                        START.minusHours(2), START.plusHours(10))).getStatusCode());
    }

    private Long acceptSeedMatch() {
        List<Map<String, Object>> proposed = service.generateMatches(OWNER, 1L);
        assertFalse(proposed.isEmpty());
        assertTrue(proposed.get(0).get("reasons").toString().contains("Capacity"));
        Long matchId = ((Number) proposed.get(0).get("id")).longValue();
        service.decide(OWNER, matchId, true, "127.0.0.1", "JUnit");
        return matchId;
    }

    private void completeTrip(Long matchId) {
        for (TrackingStatus status : List.of(
                TrackingStatus.DRIVER_ASSIGNED,
                TrackingStatus.EN_ROUTE_TO_PICKUP,
                TrackingStatus.CARGO_COLLECTED,
                TrackingStatus.IN_TRANSIT,
                TrackingStatus.DELIVERED,
                TrackingStatus.COMPLETED)) {
            service.addTracking(TRANSPORTER, matchId, status, null, null);
        }
    }
}
