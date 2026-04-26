package com.example.bloodbanksystem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    List<Donation> findByDonorId(Long donorId);
    List<Donation> findByBloodGroup(String bloodGroup);
    List<Donation> findByDonorIdOrderByDonationDateDesc(Long donorId);
}