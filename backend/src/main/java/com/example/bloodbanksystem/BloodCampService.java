package com.example.bloodbanksystem;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BloodCampService {

    private final BloodCampRepository campRepository;
    private final CampRegistrationRepository registrationRepository;
    private final DonorRepository donorRepository;
    private final DonationService donationService;

    public BloodCampService(BloodCampRepository campRepository, 
                            CampRegistrationRepository registrationRepository,
                            DonorRepository donorRepository,
                            DonationService donationService) {
        this.campRepository = campRepository;
        this.registrationRepository = registrationRepository;
        this.donorRepository = donorRepository;
        this.donationService = donationService;
    }

    public List<BloodCamp> getAllCamps() {
        return campRepository.findAllByOrderByEventDateDesc();
    }

    public Optional<BloodCamp> getCampById(Long id) {
        return campRepository.findById(id);
    }

    public BloodCamp createCamp(BloodCamp camp) {
        if (camp.getStatus() == null) {
            camp.setStatus("UPCOMING");
        }
        return campRepository.save(camp);
    }

    public BloodCamp updateCamp(Long id, BloodCamp campDetails) {
        BloodCamp camp = campRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Camp not found"));

        camp.setName(campDetails.getName());
        camp.setLocation(campDetails.getLocation());
        camp.setEventDate(campDetails.getEventDate());
        camp.setStartTime(campDetails.getStartTime());
        camp.setEndTime(campDetails.getEndTime());
        camp.setOrganizer(campDetails.getOrganizer());
        camp.setContactNumber(campDetails.getContactNumber());
        camp.setCapacity(campDetails.getCapacity());
        camp.setStatus(campDetails.getStatus());
        camp.setNotes(campDetails.getNotes());

        return campRepository.save(camp);
    }

    public void deleteCamp(Long id) {
        campRepository.deleteById(id);
    }

    @Transactional
    public CampRegistration registerDonorForCamp(Long campId, Long donorId, String slotTime) {
        BloodCamp camp = campRepository.findById(campId)
                .orElseThrow(() -> new IllegalArgumentException("Camp not found"));

        if ("CANCELLED".equals(camp.getStatus()) || "COMPLETED".equals(camp.getStatus())) {
            throw new IllegalArgumentException("Cannot register for a " + camp.getStatus().toLowerCase() + " camp");
        }

        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new IllegalArgumentException("Donor not found"));

        // 1. Check duplicate registration
        if (registrationRepository.existsByDonorIdAndBloodCampId(donorId, campId)) {
            throw new IllegalArgumentException("Donor is already registered for this camp");
        }

        // 2. Check donor eligibility (90-day donation window)
        if (!donationService.isDonorEligible(donorId)) {
            LocalDate nextDate = donationService.getNextEligibleDate(donorId);
            throw new IllegalArgumentException("Donor is not currently eligible. Next eligible date: " + nextDate);
        }

        // 3. Check camp capacity limit
        long activeRegistrations = registrationRepository.countByBloodCampId(campId);
        if (activeRegistrations >= camp.getCapacity()) {
            throw new IllegalArgumentException("Camp has reached its maximum capacity of " + camp.getCapacity() + " slots");
        }

        CampRegistration registration = new CampRegistration(donor, camp, slotTime);
        return registrationRepository.save(registration);
    }

    public List<CampRegistration> getRegistrationsForCamp(Long campId) {
        return registrationRepository.findByBloodCampId(campId);
    }

    @Transactional
    public CampRegistration updateRegistrationStatus(Long regId, String status, String healthStatus) {
        CampRegistration registration = registrationRepository.findById(regId)
                .orElseThrow(() -> new IllegalArgumentException("Registration not found"));

        String oldStatus = registration.getStatus();
        registration.setStatus(status.toUpperCase());
        registration.setHealthStatus(healthStatus.toUpperCase());

        CampRegistration saved = registrationRepository.save(registration);

        // Auto donation logger: if marked as ATTENDED and health check is ELIGIBLE
        // and it wasn't already attended, record a donation!
        if ("ATTENDED".equalsIgnoreCase(status) && "ELIGIBLE".equalsIgnoreCase(healthStatus) && !"ATTENDED".equalsIgnoreCase(oldStatus)) {
            Donor donor = registration.getDonor();
            BloodCamp camp = registration.getBloodCamp();

            Donation donation = new Donation();
            donation.setDonor(donor);
            donation.setDonationDate(camp.getEventDate());
            donation.setAmount(450); // Default standard donation size
            donation.setBloodGroup(donor.getBloodGroup());
            donation.setNotes("Auto-logged donation from camp: " + camp.getName());
            donation.setStatus(Donation.DonationStatus.COMPLETED);

            donationService.saveDonation(donation);
        }

        return saved;
    }

    public void cancelRegistration(Long regId) {
        registrationRepository.deleteById(regId);
    }
}
