package com.example.bloodbanksystem;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class DonationService {

    private final DonationRepository donationRepository;
    private final DonorRepository donorRepository;
    private final InventoryService inventoryService;

    public DonationService(DonationRepository donationRepository, DonorRepository donorRepository, InventoryService inventoryService) {
        this.donationRepository = donationRepository;
        this.donorRepository = donorRepository;
        this.inventoryService = inventoryService;
    }

    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    public Optional<Donation> getDonationById(Long id) {
        return donationRepository.findById(id);
    }

    public List<Donation> getDonationsByDonorId(Long donorId) {
        return donationRepository.findByDonorIdOrderByDonationDateDesc(donorId);
    }

    public Donation saveDonation(@Valid Donation donation) {
        // Validate that donor exists
        Donor donor = donorRepository.findById(donation.getDonor().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid donor"));

        donation.setDonor(donor);

        // Ensure blood group matches donor's blood group
        if (donation.getBloodGroup() == null || donation.getBloodGroup().isEmpty()) {
            donation.setBloodGroup(donor.getBloodGroup());
        }

        // Check donor eligibility (90 days gap)
        if (!isDonorEligible(donor.getId())) {
            throw new IllegalArgumentException("Donor is not eligible. Must wait 90 days between donations.");
        }

        Donation saved = donationRepository.save(donation);

        // Update inventory: convert ml to units (1 unit = ~450ml)
        int units = donation.getAmount() >= 350 ? 1 : 0;
        if (units > 0) {
            inventoryService.addUnits(donation.getBloodGroup(), units);
        }

        return saved;
    }

    public boolean isDonorEligible(Long donorId) {
        List<Donation> donations = donationRepository.findByDonorIdOrderByDonationDateDesc(donorId);
        if (donations.isEmpty()) return true;

        LocalDate lastDonation = donations.get(0).getDonationDate();
        return lastDonation.plusDays(90).isBefore(LocalDate.now()) || lastDonation.plusDays(90).isEqual(LocalDate.now());
    }

    public LocalDate getNextEligibleDate(Long donorId) {
        List<Donation> donations = donationRepository.findByDonorIdOrderByDonationDateDesc(donorId);
        if (donations.isEmpty()) return LocalDate.now();
        return donations.get(0).getDonationDate().plusDays(90);
    }

    public Donation updateDonation(Long id, @Valid Donation donationDetails) {
        Optional<Donation> optionalDonation = donationRepository.findById(id);
        if (optionalDonation.isPresent()) {
            Donation donation = optionalDonation.get();
            donation.setDonationDate(donationDetails.getDonationDate());
            donation.setAmount(donationDetails.getAmount());
            donation.setBloodGroup(donationDetails.getBloodGroup());
            donation.setNotes(donationDetails.getNotes());
            return donationRepository.save(donation);
        } else {
            throw new IllegalArgumentException("Donation not found");
        }
    }

    public void deleteDonation(Long id) {
        donationRepository.deleteById(id);
    }

    public List<Donation> searchByBloodGroup(String bloodGroup) {
        return donationRepository.findByBloodGroup(bloodGroup.toUpperCase());
    }
}