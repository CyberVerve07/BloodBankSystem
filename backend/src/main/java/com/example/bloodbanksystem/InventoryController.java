package com.example.bloodbanksystem;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<BloodInventory>> getInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/{bloodGroup}")
    public ResponseEntity<?> getByBloodGroup(@PathVariable String bloodGroup) {
        return inventoryService.getByBloodGroup(bloodGroup)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
