package com.example.bloodbanksystem;

import jakarta.persistence.*;

@Entity
@Table(name = "blood_inventory")
public class BloodInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "blood_group", unique = true, nullable = false)
    private String bloodGroup;

    @Column(nullable = false)
    private Integer unitsAvailable = 0;

    @Column(nullable = false)
    private Integer totalCollected = 0;

    @Column(nullable = false)
    private Integer totalUsed = 0;

    public BloodInventory() {}

    public BloodInventory(String bloodGroup) {
        this.bloodGroup = bloodGroup;
        this.unitsAvailable = 0;
        this.totalCollected = 0;
        this.totalUsed = 0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public Integer getUnitsAvailable() { return unitsAvailable; }
    public void setUnitsAvailable(Integer unitsAvailable) { this.unitsAvailable = unitsAvailable; }

    public Integer getTotalCollected() { return totalCollected; }
    public void setTotalCollected(Integer totalCollected) { this.totalCollected = totalCollected; }

    public Integer getTotalUsed() { return totalUsed; }
    public void setTotalUsed(Integer totalUsed) { this.totalUsed = totalUsed; }
}
