package com.example.bloodbanksystem;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donors")
public class DonorController {

    private final DonorService donorService;
    private final DonationService donationService;

    public DonorController(DonorService donorService, DonationService donationService) {
        this.donorService = donorService;
        this.donationService = donationService;
    }

    @GetMapping
    public ResponseEntity<List<Donor>> listDonors(
            @RequestParam(value = "bloodGroup", required = false) String bloodGroup) {
        List<Donor> donors;
        if (bloodGroup != null && !bloodGroup.isEmpty()) {
            donors = donorService.searchByBloodGroup(bloodGroup.toUpperCase());
        } else {
            donors = donorService.getAllDonors();
        }
        return ResponseEntity.ok(donors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDonor(@PathVariable Long id) {
        return donorService.getDonorById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/eligibility")
    public ResponseEntity<?> checkEligibility(@PathVariable Long id) {
        boolean eligible = donationService.isDonorEligible(id);
        var nextDate = donationService.getNextEligibleDate(id);
        return ResponseEntity.ok(Map.of(
            "eligible", eligible,
            "nextEligibleDate", nextDate.toString()
        ));
    }

    @PostMapping
    public ResponseEntity<?> addDonor(@Valid @RequestBody Donor donor) {
        try {
            Donor saved = donorService.saveDonor(donor);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDonor(@PathVariable Long id, @Valid @RequestBody Donor donor) {
        try {
            Donor updated = donorService.updateDonor(id, donor);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDonor(@PathVariable Long id) {
        donorService.deleteDonor(id);
        return ResponseEntity.ok(Map.of("message", "Donor deleted successfully"));
    }
}