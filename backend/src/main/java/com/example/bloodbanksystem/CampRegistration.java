package com.example.bloodbanksystem;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "camp_registrations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"donor_id", "camp_id"})
})
public class CampRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "camp_id", nullable = false)
    private BloodCamp bloodCamp;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate = LocalDateTime.now();

    @Column(name = "slot_time")
    private String slotTime; // e.g. "10:00 AM - 11:00 AM"

    @Column(nullable = false)
    private String status = "REGISTERED"; // REGISTERED, ATTENDED, NOSHOW, CANCELLED

    @Column(name = "health_status")
    private String healthStatus = "PENDING"; // PENDING, ELIGIBLE, INELIGIBLE

    public CampRegistration() {}

    public CampRegistration(Donor donor, BloodCamp bloodCamp, String slotTime) {
        this.donor = donor;
        this.bloodCamp = bloodCamp;
        this.slotTime = slotTime;
        this.registrationDate = LocalDateTime.now();
        this.status = "REGISTERED";
        this.healthStatus = "PENDING";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Donor getDonor() { return donor; }
    public void setDonor(Donor donor) { this.donor = donor; }

    public BloodCamp getBloodCamp() { return bloodCamp; }
    public void setBloodCamp(BloodCamp bloodCamp) { this.bloodCamp = bloodCamp; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public String getSlotTime() { return slotTime; }
    public void setSlotTime(String slotTime) { this.slotTime = slotTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }
}
