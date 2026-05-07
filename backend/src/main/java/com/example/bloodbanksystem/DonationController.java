package com.example.bloodbanksystem;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donations")
public class DonationController {

    private final DonationService donationService;
    private final DonorService donorService;

    public DonationController(DonationService donationService, DonorService donorService) {
        this.donationService = donationService;
        this.donorService = donorService;
    }

    @GetMapping
    public ResponseEntity<List<Donation>> listDonations(
            @RequestParam(value = "bloodGroup", required = false) String bloodGroup) {
        List<Donation> donations;
        if (bloodGroup != null && !bloodGroup.isEmpty()) {
            donations = donationService.searchByBloodGroup(bloodGroup);
        } else {
            donations = donationService.getAllDonations();
        }
        return ResponseEntity.ok(donations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDonation(@PathVariable Long id) {
        return donationService.getDonationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/donor/{donorId}")
    public ResponseEntity<List<Donation>> getDonationsByDonor(@PathVariable Long donorId) {
        return ResponseEntity.ok(donationService.getDonationsByDonorId(donorId));
    }

    @PostMapping
    public ResponseEntity<?> addDonation(@RequestBody Map<String, Object> payload) {
        try {
            Long donorId = Long.valueOf(payload.get("donorId").toString());
            Donor donor = donorService.getDonorById(donorId)
                    .orElseThrow(() -> new IllegalArgumentException("Donor not found"));

            Donation donation = new Donation();
            donation.setDonor(donor);
            donation.setDonationDate(java.time.LocalDate.parse(payload.get("donationDate").toString()));
            donation.setAmount(Integer.valueOf(payload.get("amount").toString()));
            donation.setBloodGroup(payload.getOrDefault("bloodGroup", donor.getBloodGroup()).toString());
            donation.setNotes(payload.getOrDefault("notes", "").toString());

            Donation saved = donationService.saveDonation(donation);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDonation(@PathVariable Long id, @Valid @RequestBody Donation donation) {
        try {
            Donation updated = donationService.updateDonation(id, donation);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDonation(@PathVariable Long id) {
        donationService.deleteDonation(id);
        return ResponseEntity.ok(Map.of("message", "Donation deleted successfully"));
    }
}