package za.co.ice.tamp.mvp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.Operation;
import za.co.ice.tamp.domain.ComplianceStatus;
import za.co.ice.tamp.domain.DisputeStatus;
import za.co.ice.tamp.domain.TrackingStatus;
import za.co.ice.tamp.domain.TruckType;

@RestController
@RequestMapping("/api")
public class MvpController {

    private final MvpService service;

    public MvpController(MvpService service) {
        this.service = service;
    }

    @Operation(summary = "Return the authenticated user's profile.")
    @GetMapping("/users/me")
    Map<String, Object> profile(Authentication auth) {
        return service.profile(auth.getName());
    }

    @Operation(summary = "Update the authenticated user's profile.")
    @PutMapping("/users/me")
    Map<String, Object> updateProfile(
            Authentication auth, @Valid @RequestBody ProfileRequest request) {
        return service.updateProfile(auth.getName(), request.name());
    }

    @Operation(summary = "Submit a compliance document for the authenticated user.")
    @PostMapping("/users/me/compliance-documents")
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> submitDocument(
            Authentication auth, @Valid @RequestBody DocumentRequest request) {
        return service.submitDocument(
                auth.getName(), request.documentType(), request.documentReference());
    }

    @Operation(summary = "Create a freight load.")
    @PostMapping("/loads")
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> createLoad(
            Authentication auth, @Valid @RequestBody LoadRequest request) {
        return service.createLoad(
                auth.getName(), request.origin(), request.destination(), request.cargoType(),
                request.weight(), request.volume(), request.pickupStart(), request.pickupEnd());
    }

    @Operation(summary = "List loads available to the authenticated user.")
    @GetMapping("/loads")
    List<Map<String, Object>> loads(Authentication auth) {
        return service.listLoads(auth.getName());
    }

    @Operation(summary = "Return a load by ID.")
    @GetMapping("/loads/{id}")
    Map<String, Object> load(Authentication auth, @PathVariable Long id) {
        return service.getLoad(auth.getName(), id);
    }

    @Operation(summary = "Update a freight load.")
    @PutMapping("/loads/{id}")
    Map<String, Object> updateLoad(
            Authentication auth, @PathVariable Long id,
            @Valid @RequestBody LoadRequest request) {
        return service.updateLoad(
                auth.getName(), id, request.origin(), request.destination(),
                request.cargoType(), request.weight(), request.volume(),
                request.pickupStart(), request.pickupEnd());
    }

    @Operation(summary = "Create an available truck.")
    @PostMapping("/trucks")
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> createTruck(
            Authentication auth, @Valid @RequestBody TruckRequest request) {
        return service.createTruck(
                auth.getName(), request.type(), request.capacity(),
                request.currentLocation(), request.availabilityStart(),
                request.availabilityEnd());
    }

    @Operation(summary = "List trucks available to the authenticated user.")
    @GetMapping("/trucks")
    List<Map<String, Object>> trucks(Authentication auth) {
        return service.listTrucks(auth.getName());
    }

    @Operation(summary = "Return a truck by ID.")
    @GetMapping("/trucks/{id}")
    Map<String, Object> truck(Authentication auth, @PathVariable Long id) {
        return service.getTruck(auth.getName(), id);
    }

    @Operation(summary = "Update an available truck.")
    @PutMapping("/trucks/{id}")
    Map<String, Object> updateTruck(
            Authentication auth, @PathVariable Long id,
            @Valid @RequestBody TruckRequest request) {
        return service.updateTruck(
                auth.getName(), id, request.type(), request.capacity(),
                request.currentLocation(), request.availabilityStart(),
                request.availabilityEnd());
    }

    @Operation(summary = "Generate eligible truck matches for a load.")
    @PostMapping("/loads/{loadId}/matches")
    List<Map<String, Object>> generateMatches(
            Authentication auth, @PathVariable Long loadId) {
        return service.generateMatches(auth.getName(), loadId);
    }

    @Operation(summary = "Return a match by ID.")
    @GetMapping("/matches/{id}")
    Map<String, Object> match(Authentication auth, @PathVariable Long id) {
        return service.getMatch(auth.getName(), id);
    }

    @Operation(summary = "Accept a proposed match.")
    @PostMapping("/matches/{id}/accept")
    Map<String, Object> accept(
            Authentication auth, @PathVariable Long id, HttpServletRequest request) {
        return service.decide(
                auth.getName(), id, true, request.getRemoteAddr(),
                request.getHeader("User-Agent"));
    }

    @Operation(summary = "Reject a proposed match.")
    @PostMapping("/matches/{id}/reject")
    Map<String, Object> reject(
            Authentication auth, @PathVariable Long id, HttpServletRequest request) {
        return service.decide(
                auth.getName(), id, false, request.getRemoteAddr(),
                request.getHeader("User-Agent"));
    }

    @Operation(summary = "Return the receipt for an accepted match.")
    @GetMapping("/matches/{id}/receipt")
    Map<String, Object> receipt(Authentication auth, @PathVariable Long id) {
        return service.receipt(auth.getName(), id);
    }

    @Operation(summary = "Record a tracking event for a match.")
    @PostMapping("/matches/{id}/tracking-events")
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> addTracking(
            Authentication auth, @PathVariable Long id,
            @Valid @RequestBody TrackingRequest request) {
        return service.addTracking(
                auth.getName(), id, request.status(),
                request.latitude(), request.longitude());
    }

    @Operation(summary = "List tracking events for a match.")
    @GetMapping("/matches/{id}/tracking-events")
    List<Map<String, Object>> tracking(Authentication auth, @PathVariable Long id) {
        return service.tracking(auth.getName(), id);
    }

    @Operation(summary = "Submit a rating for a completed match.")
    @PostMapping("/matches/{id}/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> rate(
            Authentication auth, @PathVariable Long id,
            @Valid @RequestBody RatingRequest request) {
        return service.rate(auth.getName(), id, request.score(), request.comment());
    }

    @Operation(summary = "Open a dispute for a match.")
    @PostMapping("/matches/{id}/disputes")
    @ResponseStatus(HttpStatus.CREATED)
    Map<String, Object> dispute(
            Authentication auth, @PathVariable Long id,
            @Valid @RequestBody DisputeRequest request) {
        return service.dispute(auth.getName(), id, request.reason());
    }

    @Operation(summary = "List all platform users.")
    @GetMapping("/admin/users")
    List<Map<String, Object>> adminUsers() {
        return service.adminUsers();
    }

    @Operation(summary = "Update a user's compliance status.")
    @PatchMapping("/admin/users/{id}/compliance-status")
    Map<String, Object> compliance(
            Authentication auth, @PathVariable Long id,
            @Valid @RequestBody ComplianceRequest request) {
        return service.updateCompliance(auth.getName(), id, request.status());
    }

    @Operation(summary = "List all platform disputes.")
    @GetMapping("/admin/disputes")
    List<Map<String, Object>> adminDisputes() {
        return service.adminDisputes();
    }

    @Operation(summary = "Update a dispute's status.")
    @PatchMapping("/admin/disputes/{id}/status")
    Map<String, Object> disputeStatus(
            Authentication auth, @PathVariable Long id,
            @Valid @RequestBody DisputeStatusRequest request) {
        return service.updateDispute(auth.getName(), id, request.status());
    }

    @Operation(summary = "List platform audit events.")
    @GetMapping("/admin/audit-events")
    List<Map<String, Object>> audits() {
        return service.adminAudits();
    }

    @Operation(summary = "Return platform activity metrics.")
    @GetMapping("/admin/metrics")
    Map<String, Object> metrics() {
        return service.metrics();
    }

    public record ProfileRequest(@NotBlank @Size(max = 100) String name) {
    }

    public record DocumentRequest(
            @NotBlank @Size(max = 50) String documentType,
            @NotBlank @Size(max = 255) String documentReference) {
    }

    public record LoadRequest(
            @NotBlank @Size(max = 100) String origin,
            @NotBlank @Size(max = 100) String destination,
            @NotBlank @Size(max = 50) String cargoType,
            @NotNull @DecimalMin(value = "0.01") BigDecimal weight,
            @NotNull @DecimalMin(value = "0.01") BigDecimal volume,
            @NotNull LocalDateTime pickupStart,
            @NotNull LocalDateTime pickupEnd) {
    }

    public record TruckRequest(
            @NotNull TruckType type,
            @NotNull @DecimalMin(value = "0.01") BigDecimal capacity,
            @NotBlank @Size(max = 100) String currentLocation,
            @NotNull LocalDateTime availabilityStart,
            @NotNull LocalDateTime availabilityEnd) {
    }

    public record TrackingRequest(
            @NotNull TrackingStatus status,
            @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
            @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude) {
    }

    public record RatingRequest(
            @Min(1) @Max(5) int score,
            @Size(max = 500) String comment) {
    }

    public record DisputeRequest(@NotBlank @Size(max = 1000) String reason) {
    }

    public record ComplianceRequest(@NotNull ComplianceStatus status) {
    }

    public record DisputeStatusRequest(@NotNull DisputeStatus status) {
    }
}
