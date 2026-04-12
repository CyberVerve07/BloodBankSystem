package com.example.bloodbanksystem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {

    Optional<Donor> findByEmail(String email);
    Optional<Donor> findByContact(String contact);
    List<Donor> findByBloodGroup(String bloodGroup);
}