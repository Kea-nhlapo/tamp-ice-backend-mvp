package za.co.ice.tamp.mvp;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import za.co.ice.tamp.domain.Load;
import za.co.ice.tamp.domain.Truck;
import za.co.ice.tamp.domain.TruckType;
import za.co.ice.tamp.domain.User;
import za.co.ice.tamp.domain.UserRole;

class MatchingRulesTest {

    private final MatchingRules rules = new MatchingRules();
    private final User owner = new User("Owner", "owner@test", "hash", UserRole.FREIGHT_OWNER);
    private final User transporter =
            new User("Transporter", "truck@test", "hash", UserRole.TRANSPORTER);
    private final LocalDateTime start = LocalDateTime.of(2026, 7, 30, 8, 0);
    private final LocalDateTime end = start.plusHours(8);

    @Test
    void validTruckIsEligibleWithReadableReasons() {
        MatchingRules.MatchEvaluation result = rules.evaluate(load("GENERAL"), truck(
                TruckType.BOX_TRUCK, "8000", "Johannesburg",
                start.minusHours(1), end.plusHours(1)));

        assertTrue(result.eligible());
        assertTrue(result.reasons().size() == 4);
    }

    @Test
    void insufficientCapacityIsRejected() {
        assertFalse(rules.evaluate(load("GENERAL"), truck(
                TruckType.BOX_TRUCK, "1000", "Johannesburg", start, end)).eligible());
    }

    @Test
    void incompatibleTruckTypeIsRejected() {
        assertFalse(rules.evaluate(load("FROZEN FOOD"), truck(
                TruckType.BOX_TRUCK, "8000", "Johannesburg", start, end)).eligible());
    }

    @Test
    void nonOverlappingAvailabilityIsRejected() {
        assertFalse(rules.evaluate(load("GENERAL"), truck(
                TruckType.BOX_TRUCK, "8000", "Johannesburg",
                end.plusDays(1), end.plusDays(2))).eligible());
    }

    @Test
    void wrongLocationIsRejected() {
        assertFalse(rules.evaluate(load("GENERAL"), truck(
                TruckType.BOX_TRUCK, "8000", "Cape Town", start, end)).eligible());
    }

    private Load load(String cargoType) {
        return new Load(owner, "Johannesburg", "Durban", cargoType,
                new BigDecimal("5000"), new BigDecimal("20"), start, end);
    }

    private Truck truck(
            TruckType type, String capacity, String location,
            LocalDateTime availableStart, LocalDateTime availableEnd) {
        return new Truck(transporter, type, new BigDecimal(capacity), location,
                availableStart, availableEnd);
    }
}
