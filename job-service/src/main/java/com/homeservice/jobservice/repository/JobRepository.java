package com.homeservice.jobservice.repository;

import com.homeservice.jobservice.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByCustomerId(Long customerId);

    List<Job> findByProviderId(Long providerId);

    List<Job> findByServiceTypeAndStatus(String serviceType, String status);
}
