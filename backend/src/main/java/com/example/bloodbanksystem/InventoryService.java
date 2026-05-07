package com.example.bloodbanksystem;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final BloodInventoryRepository inventoryRepository;

    public InventoryService(BloodInventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<BloodInventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public Optional<BloodInventory> getByBloodGroup(String bloodGroup) {
        return inventoryRepository.findByBloodGroup(bloodGroup);
    }

    public void addUnits(String bloodGroup, int units) {
        BloodInventory inventory = inventoryRepository.findByBloodGroup(bloodGroup)
                .orElse(new BloodInventory(bloodGroup));
        inventory.setUnitsAvailable(inventory.getUnitsAvailable() + units);
        inventory.setTotalCollected(inventory.getTotalCollected() + units);
        inventoryRepository.save(inventory);
    }

    public boolean useUnits(String bloodGroup, int units) {
        Optional<BloodInventory> optInventory = inventoryRepository.findByBloodGroup(bloodGroup);
        if (optInventory.isPresent() && optInventory.get().getUnitsAvailable() >= units) {
            BloodInventory inventory = optInventory.get();
            inventory.setUnitsAvailable(inventory.getUnitsAvailable() - units);
            inventory.setTotalUsed(inventory.getTotalUsed() + units);
            inventoryRepository.save(inventory);
            return true;
        }
        return false;
    }

    public void initializeInventory() {
        String[] bloodGroups = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        for (String bg : bloodGroups) {
            if (inventoryRepository.findByBloodGroup(bg).isEmpty()) {
                inventoryRepository.save(new BloodInventory(bg));
            }
        }
    }
}
