package com.example.bloodbanksystem;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Entity
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @NotNull(message = "Donation date is required")
    private LocalDate donationDate;

    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be at least 1 ml")
    @Max(value = 500, message = "Amount cannot exceed 500 ml")
    private Integer amount; // in ml

    @NotBlank(message = "Blood group is required")
    private String bloodGroup;

    private String notes;

    // Constructors
    public Donation() {}

    public Donation(Donor donor, LocalDate donationDate, Integer amount, String bloodGroup, String notes) {
        this.donor = donor;
        this.donationDate = donationDate;
        this.amount = amount;
        this.bloodGroup = bloodGroup;
        this.notes = notes;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Donor getDonor() { return donor; }
    public void setDonor(Donor donor) { this.donor = donor; }

    public LocalDate getDonationDate() { return donationDate; }
    public void setDonationDate(LocalDate donationDate) { this.donationDate = donationDate; }

    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}