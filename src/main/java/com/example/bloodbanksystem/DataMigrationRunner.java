package com.example.bloodbanksystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

@Component
public class DataMigrationRunner implements CommandLineRunner {

    @Autowired
    private DonorService donorService;

    // Temporary class to match old serialized Donor
    static class OldDonor implements Serializable {
        UUID id;
        String name;
        String fatherName;
        String email;
        String bloodGroup;
        String contact;

        public String getName() { return name; }
        public String getFatherName() { return fatherName; }
        public String getEmail() { return email; }
        public String getBloodGroup() { return bloodGroup; }
        public String getContact() { return contact; }
    }

    @Override
    public void run(String... args) throws Exception {
        // Check if donors.dat exists and migrate data
        java.io.File file = new java.io.File("donors.dat");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                @SuppressWarnings("unchecked")
                ArrayList<OldDonor> oldDonors = (ArrayList<OldDonor>) ois.readObject();
                for (OldDonor oldDonor : oldDonors) {
                    Donor newDonor = new Donor(
                        oldDonor.getName(),
                        oldDonor.getFatherName(),
                        oldDonor.getEmail(),
                        oldDonor.getBloodGroup(),
                        oldDonor.getContact()
                    );
                    try {
                        donorService.saveDonor(newDonor);
                    } catch (IllegalArgumentException e) {
                        // Skip duplicates or invalid data
                        System.out.println("Skipping donor: " + e.getMessage());
                    }
                }
                System.out.println("Data migration completed. You can delete donors.dat if not needed.");
            } catch (Exception e) {
                System.out.println("Error during data migration: " + e.getMessage());
            }
        }
    }
}