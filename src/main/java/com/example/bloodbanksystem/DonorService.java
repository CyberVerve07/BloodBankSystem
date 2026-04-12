package com.example.bloodbanksystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class DonorService {

    @Autowired
    private DonorRepository donorRepository;

    public List<Donor> getAllDonors() {
        return donorRepository.findAll();
    }

    public Optional<Donor> getDonorById(Long id) {
        return donorRepository.findById(id);
    }

    public Donor saveDonor(@Valid Donor donor) {
        // Check for duplicates
        if (donorRepository.findByEmail(donor.getEmail()).isPresent() ||
            donorRepository.findByContact(donor.getContact()).isPresent()) {
            throw new IllegalArgumentException("Duplicate donor: email or contact already exists");
        }
        return donorRepository.save(donor);
    }

    public Donor updateDonor(Long id, @Valid Donor donorDetails) {
        Optional<Donor> optionalDonor = donorRepository.findById(id);
        if (optionalDonor.isPresent()) {
            Donor donor = optionalDonor.get();
            // Check duplicates, but allow if same donor
            if (donorRepository.findByEmail(donorDetails.getEmail()).isPresent() &&
                !donor.getEmail().equals(donorDetails.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            if (donorRepository.findByContact(donorDetails.getContact()).isPresent() &&
                !donor.getContact().equals(donorDetails.getContact())) {
                throw new IllegalArgumentException("Contact already exists");
            }
            donor.setName(donorDetails.getName());
            donor.setFatherName(donorDetails.getFatherName());
            donor.setEmail(donorDetails.getEmail());
            donor.setBloodGroup(donorDetails.getBloodGroup());
            donor.setContact(donorDetails.getContact());
            return donorRepository.save(donor);
        } else {
            throw new IllegalArgumentException("Donor not found");
        }
    }

    public void deleteDonor(Long id) {
        donorRepository.deleteById(id);
    }

    public List<Donor> searchByBloodGroup(String bloodGroup) {
        return donorRepository.findByBloodGroup(bloodGroup);
    }
}