package za.co.ice.tamp.mvp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import za.co.ice.tamp.domain.AuditEvent;
import za.co.ice.tamp.domain.ComplianceDocument;
import za.co.ice.tamp.domain.ComplianceStatus;
import za.co.ice.tamp.domain.DecisionType;
import za.co.ice.tamp.domain.Dispute;
import za.co.ice.tamp.domain.DisputeStatus;
import za.co.ice.tamp.domain.Load;
import za.co.ice.tamp.domain.LoadStatus;
import za.co.ice.tamp.domain.Match;
import za.co.ice.tamp.domain.MatchStatus;
import za.co.ice.tamp.domain.Rating;
import za.co.ice.tamp.domain.Receipt;
import za.co.ice.tamp.domain.TrackingEvent;
import za.co.ice.tamp.domain.TrackingStatus;
import za.co.ice.tamp.domain.Truck;
import za.co.ice.tamp.domain.TruckStatus;
import za.co.ice.tamp.domain.TruckType;
import za.co.ice.tamp.domain.User;
import za.co.ice.tamp.domain.UserRole;
import za.co.ice.tamp.repository.AuditEventRepository;
import za.co.ice.tamp.repository.ComplianceDocumentRepository;
import za.co.ice.tamp.repository.DisputeRepository;
import za.co.ice.tamp.repository.LoadRepository;
import za.co.ice.tamp.repository.MatchRepository;
import za.co.ice.tamp.repository.RatingRepository;
import za.co.ice.tamp.repository.ReceiptRepository;
import za.co.ice.tamp.repository.TrackingEventRepository;
import za.co.ice.tamp.repository.TruckRepository;
import za.co.ice.tamp.repository.UserRepository;

@Service
@Transactional
public class MvpService {

    private final UserRepository users;
    private final ComplianceDocumentRepository documents;
    private final LoadRepository loads;
    private final TruckRepository trucks;
    private final MatchRepository matches;
    private final ReceiptRepository receipts;
    private final TrackingEventRepository tracking;
    private final RatingRepository ratings;
    private final DisputeRepository disputes;
    private final AuditEventRepository audits;
    private final MatchingRules matchingRules;

    public MvpService(
            UserRepository users,
            ComplianceDocumentRepository documents,
            LoadRepository loads,
            TruckRepository trucks,
            MatchRepository matches,
            ReceiptRepository receipts,
            TrackingEventRepository tracking,
            RatingRepository ratings,
            DisputeRepository disputes,
            AuditEventRepository audits,
            MatchingRules matchingRules) {
        this.users = users;
        this.documents = documents;
        this.loads = loads;
        this.trucks = trucks;
        this.matches = matches;
        this.receipts = receipts;
        this.tracking = tracking;
        this.ratings = ratings;
        this.disputes = disputes;
        this.audits = audits;
        this.matchingRules = matchingRules;
    }

    public Map<String, Object> profile(String email) {
        return userView(currentUser(email));
    }

    public Map<String, Object> updateProfile(String email, String name) {
        User user = currentUser(email);
        user.setName(name.trim());
        audit(user, "PROFILE_UPDATED", "User", user.getId(), null);
        return userView(user);
    }

    public Map<String, Object> submitDocument(String email, String type, String reference) {
        User user = currentUser(email);
        ComplianceDocument document = documents.save(
                new ComplianceDocument(user, type.trim(), reference.trim()));
        audit(user, "COMPLIANCE_SUBMITTED", "ComplianceDocument", document.getId(), null);
        return documentView(document);
    }

    public Map<String, Object> createLoad(
            String email, String origin, String destination, String cargoType,
            BigDecimal weight, BigDecimal volume,
            LocalDateTime pickupStart, LocalDateTime pickupEnd) {
        User owner = requireRole(currentUser(email), UserRole.FREIGHT_OWNER);
        requireWindow(pickupStart, pickupEnd);
        Load load = loads.save(new Load(
                owner, origin.trim(), destination.trim(), cargoType.trim(),
                weight, volume, pickupStart, pickupEnd));
        audit(owner, "LOAD_CREATED", "Load", load.getId(), null);
        return loadView(load);
    }

    public List<Map<String, Object>> listLoads(String email) {
        User user = currentUser(email);
        List<Load> result = user.getRole() == UserRole.FREIGHT_OWNER
                ? loads.findByOwnerId(user.getId()) : loads.findAll();
        return result.stream().map(this::loadView).toList();
    }

    public Map<String, Object> getLoad(String email, Long id) {
        currentUser(email);
        return loadView(load(id));
    }

    public Map<String, Object> updateLoad(
            String email, Long id, String origin, String destination, String cargoType,
            BigDecimal weight, BigDecimal volume,
            LocalDateTime pickupStart, LocalDateTime pickupEnd) {
        User owner = requireRole(currentUser(email), UserRole.FREIGHT_OWNER);
        Load load = load(id);
        requireOwner(owner, load.getOwner());
        if (load.getStatus() != LoadStatus.OPEN) {
            throw conflict("Only open loads can be edited");
        }
        requireWindow(pickupStart, pickupEnd);
        load.setOrigin(origin.trim());
        load.setDestination(destination.trim());
        load.setCargoType(cargoType.trim());
        load.setWeight(weight);
        load.setVolume(volume);
        load.setPickupStart(pickupStart);
        load.setPickupEnd(pickupEnd);
        audit(owner, "LOAD_UPDATED", "Load", load.getId(), null);
        return loadView(load);
    }

    public Map<String, Object> createTruck(
            String email, TruckType type, BigDecimal capacity, String location,
            LocalDateTime availableStart, LocalDateTime availableEnd) {
        User transporter = requireRole(currentUser(email), UserRole.TRANSPORTER);
        requireWindow(availableStart, availableEnd);
        Truck truck = trucks.save(new Truck(
                transporter, type, capacity, location.trim(), availableStart, availableEnd));
        audit(transporter, "TRUCK_CREATED", "Truck", truck.getId(), null);
        return truckView(truck);
    }

    public List<Map<String, Object>> listTrucks(String email) {
        User user = currentUser(email);
        List<Truck> result = user.getRole() == UserRole.TRANSPORTER
                ? trucks.findByTransporterId(user.getId()) : trucks.findAll();
        return result.stream().map(this::truckView).toList();
    }

    public Map<String, Object> getTruck(String email, Long id) {
        currentUser(email);
        return truckView(truck(id));
    }

    public Map<String, Object> updateTruck(
            String email, Long id, TruckType type, BigDecimal capacity, String location,
            LocalDateTime availableStart, LocalDateTime availableEnd) {
        User transporter = requireRole(currentUser(email), UserRole.TRANSPORTER);
        Truck truck = truck(id);
        requireOwner(transporter, truck.getTransporter());
        if (truck.getStatus() != TruckStatus.AVAILABLE) {
            throw conflict("Only available trucks can be edited");
        }
        requireWindow(availableStart, availableEnd);
        truck.setType(type);
        truck.setCapacity(capacity);
        truck.setCurrentLocation(location.trim());
        truck.setAvailabilityStart(availableStart);
        truck.setAvailabilityEnd(availableEnd);
        audit(transporter, "TRUCK_UPDATED", "Truck", truck.getId(), null);
        return truckView(truck);
    }

    public List<Map<String, Object>> generateMatches(String email, Long loadId) {
        User owner = requireRole(currentUser(email), UserRole.FREIGHT_OWNER);
        Load load = load(loadId);
        requireOwner(owner, load.getOwner());
        if (load.getStatus() != LoadStatus.OPEN) {
            throw conflict("Only open loads can generate matches");
        }
        List<Map<String, Object>> eligible = new ArrayList<>();
        for (Truck truck : trucks.findAll()) {
            if (truck.getStatus() != TruckStatus.AVAILABLE) {
                continue;
            }
            MatchingRules.MatchEvaluation evaluation = matchingRules.evaluate(load, truck);
            if (evaluation.eligible()) {
                Match match = matches.findByLoadIdAndTruckId(loadId, truck.getId())
                        .orElseGet(() -> matches.save(new Match(
                                load, truck, new BigDecimal("100.00"),
                                String.join("; ", evaluation.reasons()))));
                eligible.add(matchView(match));
            }
        }
        audit(owner, "MATCHES_GENERATED", "Load", loadId, "eligible=" + eligible.size());
        return eligible;
    }

    public Map<String, Object> getMatch(String email, Long id) {
        User user = currentUser(email);
        Match match = match(id);
        requirePartyOrAdmin(user, match);
        return matchView(match);
    }

    public Map<String, Object> decide(
            String email, Long id, boolean accept, String ip, String userAgent) {
        User actor = currentUser(email);
        Match match = match(id);
        requirePartyOrAdmin(actor, match);
        if (match.getStatus() != MatchStatus.PROPOSED) {
            throw conflict("Match has already been decided");
        }
        match.setStatus(accept ? MatchStatus.ACCEPTED : MatchStatus.REJECTED);
        if (accept) {
            match.getLoad().setStatus(LoadStatus.MATCHED);
            match.getTruck().setStatus(TruckStatus.MATCHED);
            receipts.save(new Receipt(match, actor, DecisionType.ACCEPTED, ip, userAgent));
            tracking.save(new TrackingEvent(match, TrackingStatus.ACCEPTED, null, null));
        }
        audit(actor, accept ? "MATCH_ACCEPTED" : "MATCH_REJECTED", "Match", id, null);
        return matchView(match);
    }

    public Map<String, Object> receipt(String email, Long matchId) {
        User user = currentUser(email);
        Match match = match(matchId);
        requirePartyOrAdmin(user, match);
        return receiptView(receipts.findByMatchId(matchId)
                .orElseThrow(() -> notFound("Receipt not found")));
    }

    public Map<String, Object> addTracking(
            String email, Long matchId, TrackingStatus status,
            BigDecimal latitude, BigDecimal longitude) {
        User actor = currentUser(email);
        Match match = match(matchId);
        requirePartyOrAdmin(actor, match);
        if (match.getStatus() != MatchStatus.ACCEPTED) {
            throw conflict("Tracking requires an accepted match");
        }
        List<TrackingEvent> events = tracking.findByMatchIdOrderByRecordedAtAsc(matchId);
        TrackingStatus expected = nextStatus(events.get(events.size() - 1).getStatus());
        if (status != expected) {
            throw badRequest("Next tracking status must be " + expected);
        }
        TrackingEvent event = tracking.save(new TrackingEvent(match, status, latitude, longitude));
        if (status == TrackingStatus.IN_TRANSIT) {
            match.getLoad().setStatus(LoadStatus.IN_TRANSIT);
            match.getTruck().setStatus(TruckStatus.IN_TRANSIT);
        } else if (status == TrackingStatus.COMPLETED) {
            match.getLoad().setStatus(LoadStatus.COMPLETED);
            match.getTruck().setStatus(TruckStatus.AVAILABLE);
        }
        audit(actor, "TRACKING_UPDATED", "Match", matchId, "status=" + status);
        return trackingView(event);
    }

    public List<Map<String, Object>> tracking(String email, Long matchId) {
        User user = currentUser(email);
        Match match = match(matchId);
        requirePartyOrAdmin(user, match);
        return tracking.findByMatchIdOrderByRecordedAtAsc(matchId)
                .stream().map(this::trackingView).toList();
    }

    public Map<String, Object> rate(String email, Long matchId, int score, String comment) {
        User reviewer = currentUser(email);
        Match match = match(matchId);
        requireParty(reviewer, match);
        if (ratings.existsByMatchIdAndReviewerId(matchId, reviewer.getId())) {
            throw conflict("You have already rated this trip");
        }
        boolean complete = tracking.findByMatchIdOrderByRecordedAtAsc(matchId)
                .stream().anyMatch(event -> event.getStatus() == TrackingStatus.COMPLETED);
        if (!complete) {
            throw conflict("Rating is allowed only after trip completion");
        }
        User reviewed = reviewer.getId().equals(match.getLoad().getOwner().getId())
                ? match.getTruck().getTransporter() : match.getLoad().getOwner();
        Rating rating = ratings.save(new Rating(match, reviewer, reviewed, score, comment));
        audit(reviewer, "RATING_CREATED", "Rating", rating.getId(), "score=" + score);
        return map("id", rating.getId(), "matchId", matchId, "reviewerId", reviewer.getId(),
                "reviewedUserId", reviewed.getId(), "score", score, "comment", comment);
    }

    public Map<String, Object> dispute(String email, Long matchId, String reason) {
        User creator = currentUser(email);
        Match match = match(matchId);
        requirePartyOrAdmin(creator, match);
        Dispute dispute = disputes.save(new Dispute(match, creator, reason.trim()));
        audit(creator, "DISPUTE_CREATED", "Dispute", dispute.getId(), null);
        return disputeView(dispute);
    }

    public List<Map<String, Object>> adminUsers() {
        return users.findAll().stream().map(this::userView).toList();
    }

    public Map<String, Object> updateCompliance(
            String adminEmail, Long userId, ComplianceStatus status) {
        User admin = requireRole(currentUser(adminEmail), UserRole.ADMIN);
        User user = user(userId);
        user.setComplianceStatus(status);
        audit(admin, "COMPLIANCE_STATUS_UPDATED", "User", userId, "status=" + status);
        return userView(user);
    }

    public List<Map<String, Object>> adminDisputes() {
        return disputes.findAll().stream().map(this::disputeView).toList();
    }

    public Map<String, Object> updateDispute(
            String adminEmail, Long id, DisputeStatus status) {
        User admin = requireRole(currentUser(adminEmail), UserRole.ADMIN);
        Dispute dispute = disputes.findById(id)
                .orElseThrow(() -> notFound("Dispute not found"));
        dispute.setStatus(status);
        audit(admin, "DISPUTE_STATUS_UPDATED", "Dispute", id, "status=" + status);
        return disputeView(dispute);
    }

    public List<Map<String, Object>> adminAudits() {
        return audits.findAll().stream().map(event -> map(
                "id", event.getId(),
                "actorId", event.getActor() == null ? null : event.getActor().getId(),
                "action", event.getAction(),
                "entityType", event.getEntityType(),
                "entityId", event.getEntityId(),
                "metadata", event.getMetadata(),
                "createdAt", event.getCreatedAt())).toList();
    }

    public Map<String, Object> metrics() {
        long accepted = matches.findAll().stream()
                .filter(match -> match.getStatus() == MatchStatus.ACCEPTED).count();
        long completed = tracking.findAll().stream()
                .filter(event -> event.getStatus() == TrackingStatus.COMPLETED).count();
        long openDisputes = disputes.findAll().stream()
                .filter(dispute -> dispute.getStatus() == DisputeStatus.OPEN).count();
        return map("users", users.count(), "loads", loads.count(), "trucks", trucks.count(),
                "matches", matches.count(), "acceptedMatches", accepted,
                "completedTrips", completed, "openDisputes", openDisputes);
    }

    private TrackingStatus nextStatus(TrackingStatus status) {
        TrackingStatus[] values = TrackingStatus.values();
        if (status.ordinal() == values.length - 1) {
            throw conflict("Trip is already completed");
        }
        return values[status.ordinal() + 1];
    }

    private User currentUser(String email) {
        return users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> notFound("User not found"));
    }

    private User user(Long id) {
        return users.findById(id).orElseThrow(() -> notFound("User not found"));
    }

    private Load load(Long id) {
        return loads.findById(id).orElseThrow(() -> notFound("Load not found"));
    }

    private Truck truck(Long id) {
        return trucks.findById(id).orElseThrow(() -> notFound("Truck not found"));
    }

    private Match match(Long id) {
        return matches.findById(id).orElseThrow(() -> notFound("Match not found"));
    }

    private User requireRole(User user, UserRole role) {
        if (user.getRole() != role) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, role + " role is required");
        }
        return user;
    }

    private void requireOwner(User actor, User owner) {
        if (!actor.getId().equals(owner.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this record");
        }
    }

    private void requirePartyOrAdmin(User user, Match match) {
        if (user.getRole() == UserRole.ADMIN) {
            return;
        }
        boolean party = user.getId().equals(match.getLoad().getOwner().getId())
                || user.getId().equals(match.getTruck().getTransporter().getId());
        if (!party) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a party to this match");
        }
    }

    private void requireParty(User user, Match match) {
        boolean party = user.getId().equals(match.getLoad().getOwner().getId())
                || user.getId().equals(match.getTruck().getTransporter().getId());
        if (!party) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only trip parties can perform this action"
            );
        }
    }

    private void requireWindow(LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw badRequest("End date must be after start date");
        }
    }

    private void audit(User actor, String action, String type, Object id, String metadata) {
        audits.save(new AuditEvent(actor, action, type, String.valueOf(id), metadata));
    }

    private Map<String, Object> userView(User user) {
        return map("id", user.getId(), "name", user.getName(), "email", user.getEmail(),
                "role", user.getRole(), "complianceStatus", user.getComplianceStatus(),
                "rating", user.getRating());
    }

    private Map<String, Object> documentView(ComplianceDocument document) {
        return map("id", document.getId(), "userId", document.getUser().getId(),
                "documentType", document.getDocumentType(),
                "documentReference", document.getDocumentReference(),
                "submittedAt", document.getSubmittedAt(), "status", document.getStatus());
    }

    private Map<String, Object> loadView(Load load) {
        return map("id", load.getId(), "ownerId", load.getOwner().getId(),
                "origin", load.getOrigin(), "destination", load.getDestination(),
                "cargoType", load.getCargoType(), "weight", load.getWeight(),
                "volume", load.getVolume(), "pickupStart", load.getPickupStart(),
                "pickupEnd", load.getPickupEnd(), "status", load.getStatus());
    }

    private Map<String, Object> truckView(Truck truck) {
        return map("id", truck.getId(), "transporterId", truck.getTransporter().getId(),
                "type", truck.getType(), "capacity", truck.getCapacity(),
                "currentLocation", truck.getCurrentLocation(),
                "availabilityStart", truck.getAvailabilityStart(),
                "availabilityEnd", truck.getAvailabilityEnd(), "status", truck.getStatus());
    }

    private Map<String, Object> matchView(Match match) {
        return map("id", match.getId(), "loadId", match.getLoad().getId(),
                "truckId", match.getTruck().getId(), "score", match.getScore(),
                "reasons", match.getReasons(), "status", match.getStatus(),
                "createdAt", match.getCreatedAt());
    }

    private Map<String, Object> receiptView(Receipt receipt) {
        return map("id", receipt.getId(), "contractId", receipt.getContractId(),
                "matchId", receipt.getMatch().getId(), "actorId", receipt.getActor().getId(),
                "decision", receipt.getDecision(), "decidedAt", receipt.getDecidedAt(),
                "ipAddress", receipt.getIpAddress(), "userAgent", receipt.getUserAgent());
    }

    private Map<String, Object> trackingView(TrackingEvent event) {
        return map("id", event.getId(), "matchId", event.getMatch().getId(),
                "status", event.getStatus(), "latitude", event.getLatitude(),
                "longitude", event.getLongitude(), "recordedAt", event.getRecordedAt());
    }

    private Map<String, Object> disputeView(Dispute dispute) {
        return map("id", dispute.getId(), "matchId", dispute.getMatch().getId(),
                "creatorId", dispute.getCreator().getId(), "reason", dispute.getReason(),
                "status", dispute.getStatus(), "createdAt", dispute.getCreatedAt());
    }

    private Map<String, Object> map(Object... values) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (int index = 0; index < values.length; index += 2) {
            result.put((String) values[index], values[index + 1]);
        }
        return result;
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }
}
