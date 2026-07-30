package za.co.ice.tamp.mvp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import za.co.ice.tamp.domain.Load;
import za.co.ice.tamp.domain.Truck;
import za.co.ice.tamp.domain.TruckType;

@Component
public class MatchingRules {

    public MatchEvaluation evaluate(Load load, Truck truck) {
        List<String> reasons = new ArrayList<>();
        if (truck.getCapacity().compareTo(load.getWeight()) >= 0) {
            reasons.add("Capacity is sufficient");
        }
        if (compatible(load.getCargoType(), truck.getType())) {
            reasons.add("Truck type is compatible");
        }
        if (!truck.getAvailabilityEnd().isBefore(load.getPickupStart())
                && !truck.getAvailabilityStart().isAfter(load.getPickupEnd())) {
            reasons.add("Availability overlaps pickup window");
        }
        if (truck.getCurrentLocation().equalsIgnoreCase(load.getOrigin())) {
            reasons.add("Truck location matches load origin");
        }
        return new MatchEvaluation(reasons.size() == 4, reasons);
    }

    private boolean compatible(String cargoType, TruckType truckType) {
        String cargo = cargoType.toUpperCase();
        if (cargo.contains("FROZEN") || cargo.contains("PERISHABLE")) {
            return truckType == TruckType.REFRIGERATED;
        }
        if (cargo.contains("LIQUID") || cargo.contains("FUEL")) {
            return truckType == TruckType.TANKER;
        }
        if (cargo.contains("MACHINERY") || cargo.contains("CONSTRUCTION")) {
            return truckType == TruckType.FLATBED;
        }
        return truckType == TruckType.BOX_TRUCK;
    }

    public record MatchEvaluation(boolean eligible, List<String> reasons) {
    }
}
