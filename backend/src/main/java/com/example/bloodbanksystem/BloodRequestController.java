package com.example.bloodbanksystem;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
public class BloodRequestController {

    private final BloodRequestService requestService;

    public BloodRequestController(BloodRequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public ResponseEntity<List<BloodRequest>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRequest(@PathVariable Long id) {
        return requestService.getRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<BloodRequest>> getPendingRequests() {
        return ResponseEntity.ok(requestService.getRequestsByStatus(BloodRequest.RequestStatus.PENDING));
    }

    @GetMapping("/pending/count")
    public ResponseEntity<?> getPendingCount() {
        return ResponseEntity.ok(Map.of("count", requestService.getPendingCount()));
    }

    @PostMapping
    public ResponseEntity<?> createRequest(@Valid @RequestBody BloodRequest request) {
        BloodRequest saved = requestService.createRequest(request);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveRequest(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(requestService.approveRequest(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/fulfill")
    public ResponseEntity<?> fulfillRequest(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(requestService.fulfillRequest(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            return ResponseEntity.ok(requestService.rejectRequest(id, body.getOrDefault("reason", "")));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
