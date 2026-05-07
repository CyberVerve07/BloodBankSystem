package com.example.bloodbanksystem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {
    List<BloodRequest> findByStatus(BloodRequest.RequestStatus status);
    List<BloodRequest> findByBloodGroup(String bloodGroup);
    List<BloodRequest> findAllByOrderByRequestDateDesc();
    long countByStatus(BloodRequest.RequestStatus status);
}
