package com.example.bloodbanksystem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BloodCampRepository extends JpaRepository<BloodCamp, Long> {
    List<BloodCamp> findByStatus(String status);
    List<BloodCamp> findAllByOrderByEventDateDesc();
}
