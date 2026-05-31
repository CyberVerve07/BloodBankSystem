package com.example.bloodbanksystem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CampRegistrationRepository extends JpaRepository<CampRegistration, Long> {
    List<CampRegistration> findByBloodCampId(Long campId);
    List<CampRegistration> findByDonorId(Long donorId);
    boolean existsByDonorIdAndBloodCampId(Long donorId, Long campId);
    long countByBloodCampId(Long campId);
}
