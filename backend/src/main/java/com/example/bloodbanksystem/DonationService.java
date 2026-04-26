package com.example.bloodbanksystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class DonationService {

    private final DonationRepository donationRepository;
    private final DonorRepository donorRepository;

    public DonationService(DonationRepository donationRepository, DonorRepository donorRepository) {
        this.donationRepository = donationRepository;
        this.donorRepository = donorRepository;
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
        if (donation.getDonor() == null || donorRepository.findById(donation.getDonor().getId()).isEmpty()) {
            throw new IllegalArgumentException("Invalid donor");
        }
        // Ensure blood group matches donor's blood group
        if (!donation.getBloodGroup().equalsIgnoreCase(donation.getDonor().getBloodGroup())) {
            throw new IllegalArgumentException("Donation blood group must match donor's blood group");
        }
        return donationRepository.save(donation);
    }

    public Donation updateDonation(Long id, @Valid Donation donationDetails) {
        Optional<Donation> optionalDonation = donationRepository.findById(id);
        if (optionalDonation.isPresent()) {
            Donation donation = optionalDonation.get();
            donation.setDonationDate(donationDetails.getDonationDate());
            donation.setAmount(donationDetails.getAmount());
            donation.setBloodGroup(donationDetails.getBloodGroup());
            donation.setNotes(donationDetails.getNotes());
            return saveDonation(donation);
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