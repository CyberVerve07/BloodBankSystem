package com.example.bloodbanksystem;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/camps")
public class BloodCampController {

    private final BloodCampService campService;

    public BloodCampController(BloodCampService campService) {
        this.campService = campService;
    }

    @GetMapping
    public ResponseEntity<List<BloodCamp>> getAllCamps() {
        return ResponseEntity.ok(campService.getAllCamps());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCamp(@PathVariable Long id) {
        return campService.getCampById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createCamp(@Valid @RequestBody BloodCamp camp) {
        try {
            BloodCamp saved = campService.createCamp(camp);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCamp(@PathVariable Long id, @Valid @RequestBody BloodCamp campDetails) {
        try {
            return ResponseEntity.ok(campService.updateCamp(id, campDetails));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCamp(@PathVariable Long id) {
        try {
            campService.deleteCamp(id);
            return ResponseEntity.ok(Map.of("message", "Camp deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/registrations")
    public ResponseEntity<List<CampRegistration>> getRegistrations(@PathVariable Long id) {
        return ResponseEntity.ok(campService.getRegistrationsForCamp(id));
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<?> registerDonor(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Long donorId = Long.valueOf(body.get("donorId").toString());
            String slotTime = body.getOrDefault("slotTime", "Anytime").toString();
            CampRegistration registration = campService.registerDonorForCamp(id, donorId, slotTime);
            return ResponseEntity.ok(registration);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/registrations/{regId}")
    public ResponseEntity<?> updateRegistration(@PathVariable Long regId, @RequestBody Map<String, String> body) {
        try {
            String status = body.get("status");
            String healthStatus = body.getOrDefault("healthStatus", "PENDING");
            CampRegistration updated = campService.updateRegistrationStatus(regId, status, healthStatus);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/registrations/{regId}")
    public ResponseEntity<?> cancelRegistration(@PathVariable Long regId) {
        try {
            campService.cancelRegistration(regId);
            return ResponseEntity.ok(Map.of("message", "Registration cancelled successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
