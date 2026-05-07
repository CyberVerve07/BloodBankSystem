package com.example.bloodbanksystem;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class HomeController {

    private final DonorService donorService;
    private final DonationService donationService;
    private final InventoryService inventoryService;
    private final BloodRequestService requestService;

    public HomeController(DonorService donorService, DonationService donationService,
                          InventoryService inventoryService, BloodRequestService requestService) {
        this.donorService = donorService;
        this.donationService = donationService;
        this.inventoryService = inventoryService;
        this.requestService = requestService;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        List<Donor> donors = donorService.getAllDonors();
        List<Donation> donations = donationService.getAllDonations();
        List<BloodInventory> inventory = inventoryService.getAllInventory();

        int totalUnits = inventory.stream().mapToInt(BloodInventory::getUnitsAvailable).sum();

        Map<String, Long> bloodGroupCounts = donors.stream()
                .collect(Collectors.groupingBy(Donor::getBloodGroup, Collectors.counting()));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDonors", donors.size());
        stats.put("totalDonations", donations.size());
        stats.put("totalUnitsAvailable", totalUnits);
        stats.put("pendingRequests", requestService.getPendingCount());
        stats.put("bloodGroupDistribution", bloodGroupCounts);
        stats.put("inventory", inventory);

        return ResponseEntity.ok(stats);
    }
}
